#include <stdlib.h>
#include <stdio.h>
#include <sys/types.h>
#include <sys/stat.h>
#include <fcntl.h>
#include <unistd.h>
#include <sys/ioctl.h>
#include <syslog.h>
#include <signal.h>
#include <netdb.h>
#include <arpa/inet.h>
#include <strings.h>
#include <pthread.h>
#include <semaphore.h>
#include <sys/ipc.h>
#include <sys/shm.h>
#include <sys/wait.h>
#include <sys/poll.h>
#include <string.h>
#include <signal.h>
#include <ctype.h>
#include <errno.h>
#include <endian.h>
#include "javafork.h"


pid_t daemonPID;
int sockfd;

#define RESTARTABLE(_cmd, _result) do { \
    _result = _cmd; \
} while((_result == -1) && (errno == EINTR))

static int restartableClose(int fd) {
    int err;
    RESTARTABLE(close(fd), err);
    return err;
}

static int closeSafely(int fd) {
    return (fd == -1) ? 0 : restartableClose(fd);
}


int main (int argc, char *argv[]) {
	int c; /*Getopt parameter*/
	/*Default values*/
	char *avalue = IPADDRESS; /*Address: numeric value or hostname*/
	int pvalue = PORT;        /*TCP port*/
	int qvalue = QUEUE;       /*TCP listen queue*/
	
	
	/*This process is intended to be used as a daemon, it sould be launched by the INIT process, because of that*/
	/*we are not forking it (INIT needs it)*/
	if (daemonize(argv[0], LOG_SYSLOG, LOG_PID) < 0)
		return 1;
	
	setsid();
	
	if (signal(SIGPIPE,SIG_IGN) == SIG_ERR) {
		syslog (LOG_ERR, "Error with SIGPIPE: %m");
		return 1;
	}
	
	
	opterr = 0;
	while ((c = getopt (argc, argv, "a:p:q:")) != -1) {
		switch (c) {
			case 'a':
				avalue = optarg;
				break;
			case 'p':
				pvalue = atoi(optarg);
				if ((pvalue > 65535) || (pvalue <= 0)) {
					syslog (LOG_ERR, "Port value %d out of range", pvalue);
					return 1;
				}
				break;
			case 'q':
				qvalue = atoi(optarg);
				break;
			case '?':
				if ((optopt == 'a') || (optopt == 'p') || (optopt == 'q'))
					syslog (LOG_ERR, "Option -%c requires an argument.", optopt);
				else if (isprint (optopt))
					syslog (LOG_ERR, "Invalid option '-%c'.", optopt);
				else
					syslog (LOG_ERR, "Unknown option character '\\x%x'.", optopt);
				return 1;
			default:
				abort ();
		}
	}
	/*This program does not admit options*/
	if (optind < argc) {
		syslog (LOG_ERR,"This program does not admit options just argument elements with their values.");
		return 1;
	}
	
	
	/*TODO: INIT process sending SIGTERM? Should I catch that signal?*/
	daemonPID = getpid();
	if (signal(SIGTERM, sigterm_handler) == SIG_ERR) {
		syslog (LOG_ERR, "SIGTERM signal handler failed: %m");
		return 1;
	}
	
	
	if (main_child(avalue, pvalue, qvalue) < 0 )
		return 1;
	
	return 0;
}


int main_child (char *address, int port, int queue) {
	struct protoent *protocol;			/*Network protocol*/
	struct sockaddr_in addr_server;	/*struct with the server socket address*/
	struct sockaddr_in  addr_client;/*struct with the client socket address*/
	int sockclient;									/*File descriptor for the accepted socket*/
	pthread_t idThread;							/*Thread identifier number*/
	socklen_t clilen;
	int optval;
	int returnValue = 0;						/*OK by default*/
	
	
	/*Retrieve protocol number from /etc/protocols file */
	protocol=getprotobyname("tcp");
	if (protocol == NULL) {
		syslog(LOG_ERR, "cannot map \"tcp\" to protocol number: %m");
		goto EndwithError;
	}
	
	bzero((char*) &addr_server, sizeof(addr_server));
	addr_server.sin_family = AF_INET;
	if (inet_pton(AF_INET, address, &addr_server.sin_addr.s_addr) <= 0) {
		syslog (LOG_ERR, "error inet_pton: %m");
		goto EndwithError;
	}
	
	addr_server.sin_port = htons(port);
	
	if ((sockfd = socket(AF_INET, SOCK_STREAM, protocol->p_proto)) < 0) {
		syslog (LOG_ERR, "socket creation failed: %m");
		goto EndandClosewithError; 
	}


	/*We want to avoid issues while trying to bind a socket in TIME_WAIT state.*/
	/*In this application from the client there should not be any trouble if it gets*/
	/*TIME_WAIT states, because it can connect from any source port.*/
	optval = 1;
	if(setsockopt(sockfd, SOL_SOCKET, SO_REUSEADDR, &optval, sizeof(optval)) < 0) {
		syslog (LOG_ERR, "setsockopt failed: %m");
		goto EndandClosewithError;
	}
	
	if (bind(sockfd, (struct sockaddr *) &addr_server, sizeof(addr_server)) < 0) {
		syslog (LOG_ERR, "socket bind failed: %m");
		goto EndandClosewithError;
	}
	
	if (listen (sockfd, queue) < 0 ) {
		syslog (LOG_ERR, "socket listen failed: %m");
		goto EndandClosewithError;
	}	
	
	while(1) {
		clilen =  sizeof(addr_client);
		if ((sockclient = accept (sockfd, (struct sockaddr *) &addr_client, &clilen)) < 0) {
			syslog (LOG_ERR, "socket accept failed: %m");
			goto EndandClosewithError;
		}
		if (pthread_create (&idThread, NULL, serverThread, (void *)sockclient) != 0 ) {
			syslog (LOG_ERR, "thread creation failed: %m");
		}	
	}
	
	
	EndasUsual:
		return returnValue;
	EndandClosewithError:
		close (sockfd);
	EndwithError:
		/*When there is an error.*/
		returnValue = -1;	
		goto EndasUsual;
}


int daemonize(const char *pname, int facility, int option) {
	int fd;
	
	if ( (fd = open( "/dev/tty", O_RDWR, 0) ) == -1) {
		/*We already have no tty control*/
		close(fd);
		return 0;
	}
	
	/*Sending messages to log*/
	openlog(pname, option, facility);
	/*To get a controlling tty*/
	if (ioctl(fd, TIOCNOTTY, (caddr_t)0) <0 ) {
		syslog (LOG_ERR, "Getting tty failed: %m");
		return -1;
	}
	if (close(fd) < 0) {
		syslog (LOG_ERR, "Closing tty failed: %m");
		return -1;
	}
	
	if((fd = open( "/dev/null", O_RDWR, 0) ) == -1) {
		close(fd);
		return -1;
	}
	dup2(fd,0);
	dup2(fd,1);
	dup2(fd,2);
	close(fd);
	
	return 0;
}

void *serverThread (void * arg) {
	int sock;

	/*Variables used for the select function*/
	struct timeval ptime;
	fd_set fd_read;
	int ret;

	/*Control parameters used while receiving data from the client*/
	int nLeft, nData, iPos;
	/*This buffer is intended to store the data received from the client.*/
	char buffer[1025];

	/*The command sent by the client, to be executed by this process*/	
	char *command;
	/*Store the command length*/
	uint32_t *commandLength;
	/*Store the ack*/
	uint32_t *ack;



	sock = (int) arg;

	pthread_detach(pthread_self());



	/******************************************************************************************/
	/*					Just over 1 TCP connection																										*/
	/*					COMMAND_LENGTH: Java integer 4 bytes, BIG-ENDIAN (the same as network order)	*/
	/* 					ACK: integer 4 bytes big-endian (for Java) with the received comand length		*/
	/* 					COMMAND: TPV locale character set encoding																		*/
	/* 					RESULTS: TPV locale character set encoding																		*/
	/*																																												*/
	/*							JAVA CLIENT: ------------ COMMAND_LENGTH -------> :SERVER									*/
	/*							JAVA CLIENT: <---------------- ACK -------------- :SERVER									*/ 
	/*							JAVA CLIENT: -------------- COMMAND ------------> :SERVER									*/
	/*							JAVA CLIENT: <-------------- RESULTS ------------ :SERVER									*/
	/*							JAVA CLIENT: <---------- CLOSE CONNECTION ------- :SERVER									*/
	/*																																												*/
	/******************************************************************************************/


	/*Wait 2 seconds for data coming from client.*/
	ptime.tv_sec=2;
  ptime.tv_usec=0;


	/*COMMAND LENGTH*/
	/*First of all we receive the command size as a Java integer (4 bytes primitive type)*/	
	if ((commandLength = (uint32_t *) malloc(sizeof(uint32_t))) == NULL) {
		syslog (LOG_ERR, "commandLength malloc failed: %m");
		goto Error;
	}
	bzero(buffer, sizeof(buffer));
	nLeft = sizeof(uint32_t);
	nData = iPos = 0;
	do {
		FD_ZERO(&fd_read);
		FD_SET(sock, &fd_read);
		ret = select(sock+1, &fd_read,NULL,NULL, &ptime);
		if(ret == 0) {
			syslog(LOG_INFO, "receiving command length timeout error");
			goto Error;
		}
		if(ret == -1) {
        syslog(LOG_ERR, "receiving command length select error: %m");
				goto Error;
    }
		if((nData = recv(sock, &buffer[iPos], nLeft, 0)) <= 0) {
			syslog (LOG_ERR, "command length reception failed: %m");
			goto Error;
		}
		nLeft -= nData;
		iPos += nData;
	} while (nLeft > 0);
	if (nLeft < 0) {
		syslog (LOG_INFO, "command length excessive data received, expected: 4, received: %d", (-nLeft + sizeof(uint32_t)) );
	}

	/*Retrieve integer (4 bytes) from buffer*/
	memcpy (commandLength, buffer, sizeof(uint32_t));
	/*Java sends the primitive integer using big-endian order (it is the same as network order)*/
	*commandLength = be32toh (*commandLength);


	
	/*ACK*/
	/*Sending back the command length as the ACK message*/
	if ((ack = (uint32_t *) malloc(sizeof(uint32_t))) == NULL) {
		syslog (LOG_ERR, "commandLength malloc failed: %m");
		goto ErrorAck;
  }
	*ack = htobe32(*commandLength);
	if (send(sock, ack, sizeof(uint32_t), 0) < 0) {
		syslog (LOG_ERR, "send ACK failed: %m");
		goto ErrorAck;
	}


		
	/*COMMAND*/
	/*Reserving commandLength + 1 because of the string end character*/
	if ((command = (char *) malloc(*commandLength + 1)) == NULL) {
		syslog (LOG_ERR, "command malloc failed: %m");
		goto ErrorCommand;
	} 
	bzero(command, ((*commandLength) + 1));
  nLeft = *commandLength;
  nData = iPos = 0;
  do {
    FD_ZERO(&fd_read);
    FD_SET(sock, &fd_read);
    ret = select(sock+1, &fd_read,NULL,NULL, &ptime);
    if(ret == 0) {
      syslog(LOG_INFO, "receiving command timeout error");
      goto ErrorCommand;
    }
    if(ret == -1) {
        syslog(LOG_ERR, "receiving command select error: %m");
        goto ErrorCommand;
    }
    if((nData = recv(sock, &command[iPos], nLeft, 0)) <= 0) {
    	syslog (LOG_ERR, "command reception failed: %m");
    	goto ErrorCommand;
    }
    nLeft -= nData;
    iPos += nData;
  } while (nLeft > 0);
  if (nLeft < 0) {
    syslog (LOG_INFO, "execessive command length, expected: %d, received: %d", *commandLength, (-nLeft + *commandLength));
  }


	/*RESULTS*/	
	fork_system(sock, command);	


	/*CLOSE CONNECTION AND FINISH*/


ErrorCommand:
	free(command);
ErrorAck:
	free(ack);
Error:	
  close(sock);
	free(commandLength);

	pthread_exit(0);
}


int fork_system(int socket, char *command) {
	int pid;
	int out[2], err[2];
	char buf[2000];
	char string[100];
	struct pollfd polls[2];
	int returnstatus;
	int n;
	int childreturnstatus;

	int returnValue = -1;   /*NOK by default*/

	/*Required variables in order to share memory between processes*/
	key_t keyvalue;
	int idreturnst = -1;
	/*Store the return status from the process launched using system or execve*/
	/*Using shared memory between the child and parent process*/
	int *returnst = NULL;
	/*Required variables in order to share the semaphore between processes*/
	key_t keysemaphore;
	int idsemaphore = -1;
	/*Used like a barrier: the child process just can start after sending the XML init code*/
	sem_t *semaphore = NULL;
	
	
	out[0] = out[1] = err[0] = err[1]  = -1;


	/*Creating the pipes, they will be attached to the stderr and stdout streams*/	
	if (pipe(out) < 0 || pipe(err) < 0) {
		syslog (LOG_ERR, "pipe failed: %m");
		goto EndandClosePipes;
  }


	
	/*Allocate shared memory because we can not use named semaphores*/
	keysemaphore=ftok("/bin/ls", SHAREMEMSEM);  /*the /bin/ls must exist otherwise this does not work... */
	if (keysemaphore == -1) {
		syslog (LOG_ERR, "ftok failed: %m");
		goto EndandClosePipes;
  }
	/*Attach shared memory*/
	if ((idsemaphore = shmget(keysemaphore,sizeof(sem_t), 0660 | IPC_CREAT)) < 0) {
		syslog (LOG_ERR, "semaphore initialization failed: %m");
		goto EndandReleaseSem;
	}
	if ((semaphore = (sem_t *)shmat(idsemaphore, (void *)0, 0)) < 0) {
		goto EndandReleaseSem;
	}

	if (sem_init(semaphore, 1, 1) < 0) {
		syslog (LOG_ERR, "semaphore initialization failed: %m");
		goto EndandDestroySem;
	}
	
	if (sem_wait(semaphore) < 0) {
		syslog (LOG_ERR, "semaphore wait failed: %m");
		goto EndandDestroySem;
	}



	/*Allocate shared memory for the return status code */
	keyvalue=ftok("/bin/ls", SHAREMEMKEY);  /*the /bin/ls must exist otherwise this does not work... */
	if (keyvalue == -1) {
		syslog (LOG_ERR, "ftok failed: %m");
		goto EndandDestroySem;
	}
	/*Attach shared memory*/
	if ((idreturnst=shmget(keyvalue,sizeof(int), 0660 | IPC_CREAT)) < 0) {
		syslog (LOG_ERR, "shmget failed: %m");
		goto EndandReleaseMem;
	}
	returnst = (int *)shmat(idreturnst, (void *)0, 0);
	if ((*returnst)== -1) {
		syslog (LOG_ERR, "shmat failed: %m");
		goto EndandReleaseMem;
	}
	/*By default*/
	(*returnst) = 0;


	
	if ((pid=fork()) == -1) {
		syslog (LOG_ERR, "fork failed: %m");
		goto EndandReleaseMem;
	}
	
	if (pid == 0) {
		/*Child process*/
		if ((dup2(out[1],1) < 0) || (dup2(err[1],2) < 0)) {	
			syslog (LOG_ERR, "child dup2 failed: %m");	
			exit(-1);
		}
		if (sem_wait(semaphore) < 0) {
			syslog (LOG_ERR, "child semaphore wait failed: %m");
			exit(-1);
		}
		
		returnstatus=system(command);
		
		if (WIFEXITED(returnstatus) == 1)
			(*returnst) = WEXITSTATUS(returnstatus);
		else
			(*returnst) = -1;
		exit(0);
	}
	else {
		/*Parent process*/
		polls[0].fd=out[0];
		polls[1].fd=err[0];
		polls[0].events=POLLIN;
		polls[1].events=POLLIN;
		sprintf(string,"<?xml version=\"1.0\"?>");
		send(socket,string,strlen(string),0);
		sprintf(string,"<salida>");
		send(socket,string,strlen(string),0);
		
		/*Releasing barrier, the child process can keep running*/
		if (sem_post(semaphore) < 0 ) {
			syslog (LOG_ERR, "parent error releasing barrier: %m");
			/*Beaware, sig_handler with the child process :]*/
			kill(pid, SIGTERM);
			goto EndandReleaseMem;
		}
		while(1) {
			if(poll(polls,2,100)) {
				if(polls[0].revents&&POLLIN) {
					bzero(buf,2000);
					n=read(out[0],buf,1990);
					sprintf(string,"<out><![CDATA[");
					send(socket,string,strlen(string),0);
					send(socket,buf,n,0);
					sprintf(string,"]]></out>");
					send(socket,string,strlen(string),0);
				} 
				if(polls[1].revents&&POLLIN) {
					bzero(buf,2000);
					n=read(err[0],buf,1990);
					sprintf(string,"<error><![CDATA[");
					send(socket,string,strlen(string),0);
					send(socket,buf,n,0);
					sprintf(string,"]]></error>");
					send(socket,string,strlen(string),0);
				}
				if(!polls[0].revents&&POLLIN && !polls[1].revents&&POLLIN) {
					syslog (LOG_ERR, "parent error polling pipes: %m");
					/*Beaware, sig_handler with the child process :]*/
					kill(pid, SIGTERM);
					/*I want to send an error status to the remote calling process*/
					(*returnst) = -1;
					break;
				}
			}
			else {
				/*When timeout*/
				if(waitpid(pid, &childreturnstatus, WNOHANG)) {
					/*Child is dead, we can finish the connection*/
					/*First of all, we check the exit status of our child process*/
					/*In case of error send an error status to the remote calling process*/
					if (WIFEXITED(childreturnstatus) != 1)
						(*returnst) = -1;
					break;
				}
				/*The child process is not dead, keep polling more data from stdout or stderr streams*/
			}
		}
	}
	/*Reaching this code when child finished or if error while polling pipes*/
	sprintf(string,"<ret><![CDATA[%d]]></ret>", (*returnst));
	send(socket,string,strlen(string),0);
	sprintf(string,"</salida>");
	send(socket,string,strlen(string),0);

	/*Stuff just done by the Parent process. The child process ends with exit*/
	
	returnValue = 0; /*if everything went OK*/

EndandReleaseMem:
		if (returnst != NULL) {
			/*detach memory*/
			if (shmdt ((int *)returnst) < 0)
				syslog (LOG_ERR, "returnstatus shared variable shmdt failed: %m");
		}
		/*Mark the segment to be destroyed.*/
		if (shmctl (idreturnst, IPC_RMID, (struct shmid_ds *)NULL) < 0 )
			syslog (LOG_ERR, "returnstatus shared variable shmctl failed: %m");
EndandDestroySem:
		if (sem_destroy(semaphore) <0)
			syslog (LOG_ERR, "semaphore destroy failed: %m");
EndandReleaseSem:
		/*after sem_destroy-> input/output parameter NULL?*/
		if (semaphore != NULL) {
			/*detach memory*/
			if (shmdt ((sem_t *)semaphore) < 0)
				syslog (LOG_ERR, "semaphore shmdt failed: %m");
		}
		/*Mark the segment to be destroyed.*/
		if (shmctl (idsemaphore, IPC_RMID, (struct shmid_ds *)NULL) < 0 )
			syslog (LOG_ERR, "semaphore shmctl failed: %m");
EndandClosePipes:
		closeSafely (out[0]);
		closeSafely (out[1]);
		closeSafely (err[0]);
		closeSafely (err[1]);

	return returnValue;
}


void sigterm_handler(int sig)
{
  if (daemonPID != getpid()) {
    //Do nothing
    return;
  }
  if (signal (SIGTERM, SIG_IGN) == SIG_ERR)
    {
      syslog (LOG_ERR, "signal desactivation failed");
    }


  close (sockfd);
  /*TODO: kill child processes and release allocate memory*/
  exit (0);
}

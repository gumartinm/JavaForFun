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


pid_t daemonPID;    /*Stores the daemon server PID*/
int sockfd = -1;    /*Stores the daemon server TCP socket.*/



static int restartableClose(int fd) 
{
    return TEMP_FAILURE_RETRY(close(fd));
}



static int closeSafely(int fd) 
{
    /*If we always initialize file descriptor variables with value -1*/
    /*this method should work like a charm*/
    return (fd == -1) ? 0 : restartableClose(fd);
}



int main (int argc, char *argv[]) 
{
	int c;                      /*Getopt parameter*/
	/*Default values*/
	char *avalue = IPADDRESS;   /*Address: numeric value or hostname*/
	int pvalue = PORT;          /*TCP port*/
	int qvalue = QUEUE;         /*TCP listen queue*/
	
	
	/*This process is intended to be used as a daemon, it sould be launched by the INIT process, because of that*/
	/*we are not forking it (INIT needs it)*/
	if (daemonize(argv[0], LOG_SYSLOG, LOG_PID) < 0)
		return 1;

	/*Changing session.*/	
	setsid();
	
	if (signal(SIGPIPE,SIG_IGN) == SIG_ERR) {
		syslog (LOG_ERR, "signal SIGPIPE desactivation failed: %m");
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
	
	/*INIT process sending SIGINT? Should I catch that signal?*/
	daemonPID = getpid();
	if (signal(SIGINT, sigint_handler) == SIG_ERR) {
		syslog (LOG_ERR, "SIGTERM signal handler failed: %m");
		return 1;
	}
	
	if (main_daemon (avalue, pvalue, qvalue) < 0)
		return 1;
	
	return 0;
}



int main_daemon (char *address, int port, int queue)
{
	struct protoent *protocol;          /*Network protocol*/
	struct sockaddr_in addr_server;	    /*struct with the server socket address*/
	struct sockaddr_in  addr_client;    /*struct with the client socket address*/
	int sockclient = -1;                /*File descriptor for the accepted socket*/
	pthread_t idThread;                 /*Thread identifier number*/
	socklen_t clilen;
	int optval;
	int returnValue = 0;                /*The return value from this function, OK by default*/
	
	
	/*Retrieve protocol number from /etc/protocols file */
	protocol=getprotobyname("tcp");
	if (protocol == NULL) {
		syslog(LOG_ERR, "cannot map \"tcp\" to protocol number: %m");
		goto err;
	}
	
	bzero((char*) &addr_server, sizeof(addr_server));
	addr_server.sin_family = AF_INET;
	if (inet_pton(AF_INET, address, &addr_server.sin_addr.s_addr) <= 0) {
		syslog (LOG_ERR, "error inet_pton: %m");
		goto err;
	}
	
	addr_server.sin_port = htons(port);
	
	if ((sockfd = socket(AF_INET, SOCK_STREAM, protocol->p_proto)) < 0) {
		syslog (LOG_ERR, "socket creation failed: %m");
		goto err; 
	}


	/*We want to avoid issues while trying to bind a socket in TIME_WAIT state*/
	optval = 1;
	if(setsockopt(sockfd, SOL_SOCKET, SO_REUSEADDR, &optval, sizeof(optval)) < 0) {
		syslog (LOG_ERR, "setsockopt failed: %m");
		goto err;
	}
	
	if (bind(sockfd, (struct sockaddr *) &addr_server, sizeof(addr_server)) < 0) {
		syslog (LOG_ERR, "socket bind failed: %m");
		goto err;
	}
	
	if (listen (sockfd, queue) < 0 ) {
		syslog (LOG_ERR, "socket listen failed: %m");
		goto err;
	}	
	
	while(1) {
		clilen =  sizeof(addr_client);
		if ((sockclient = TEMP_FAILURE_RETRY(accept (sockfd, (struct sockaddr *) &addr_client, &clilen))) < 0) {
			syslog (LOG_ERR, "socket accept failed: %m");
			goto err;
		}

		if (pthread_create (&idThread, NULL, serverThread, (void *)sockclient) != 0 ) {
			syslog (LOG_ERR, "thread creation failed: %m");
		}
	}

end:
    closeSafely (sockfd);
    return returnValue;
err:
	/*When there is an error.*/
	returnValue = -1;	
	goto end;
}



int daemonize(const char *pname, int facility, int option)
{
	int fd = -1;    /*Temporaly store for the /dev/tty and /dev/null file descriptors*/
	
	if ((fd = TEMP_FAILURE_RETRY(open( "/dev/tty", O_RDWR, 0))) == -1) {
/*We already have no tty control*/
closeSafely(fd);
return 0;
	}
	
	/*Sending messages to log*/
	openlog(pname, option, facility);

	/*To get a controlling tty*/
	if (ioctl(fd, TIOCNOTTY, (caddr_t)0) <0 ) {
		syslog (LOG_ERR, "Getting tty failed: %m");
		return -1;
	}

	if (closeSafely(fd) < 0) {
		syslog (LOG_ERR, "Closing tty failed: %m");
		return -1;
	}
	
	if ((fd = TEMP_FAILURE_RETRY(open( "/dev/null", O_RDWR, 0))) == -1) {
		closeSafely(fd);
		return -1;
	}

	if (TEMP_FAILURE_RETRY(dup2(fd,0)) < 0 || 
        TEMP_FAILURE_RETRY(dup2(fd,1)) < 0 ||
        TEMP_FAILURE_RETRY(dup2(fd,2)) < 0) {
	    closeSafely(fd);
        return -1;
    }

    closeSafely(fd);	

	return 0;
}



void *serverThread (void * arg)
{
	int socket = -1;                    /*Open socket by the Java client*/
	long timeout, utimeout;             /*Timeout for reading data from client: secs and usecs respectively*/
	int len;                            /*Control parameter used while receiving data from the client*/
	char buffer[1025];                  /*This buffer is intended to store the data received from the client*/
	char *command = NULL;               /*The command sent by the client, to be executed by this process*/	
	uint32_t *commandLength = NULL;     /*Store the command length*/
	
	socket = (int) arg;
	
	pthread_detach(pthread_self());


    if (required_sock_options (socket) < 0)
        goto err;
	
	
            /****************************************************************************************/
            /*   Just over 1 TCP connection                                                         */
            /*   COMMAND_LENGTH: Java integer 4 bytes, BIG-ENDIAN (the same as network order)       */
            /*   COMMAND: locale character set encoding                                             */
            /*   RESULTS: locale character set encoding                                             */
            /*                                                                                      */											        
            /*   JAVA CLIENT: ------------ COMMAND_LENGTH -------> :SERVER                          */
            /*   JAVA CLIENT: -------------- COMMAND ------------> :SERVER                          */
            /*   JAVA CLIENT: <-------------- RESULTS ------------ :SERVER                          */
            /*   JAVA CLIENT: <---------- CLOSE CONNECTION ------- :SERVER                          */
            /*                                                                                      */
            /****************************************************************************************/

    /*Wait max 2 seconds for data coming from client, otherwise exits with error.*/
    timeout = 2;
    utimeout = 0;


    /*1. COMMAND LENGTH*/
    /*First of all we receive the command size as a Java integer (4 bytes primitive type)*/	
    if ((commandLength = (uint32_t *) malloc(sizeof(uint32_t))) == NULL) {
        syslog (LOG_ERR, "commandLength malloc failed: %m");
        goto err;
    }

    bzero(buffer, sizeof(buffer));
    len = sizeof(uint32_t);

    if (receive_from_socket (socket, buffer, len, timeout, utimeout) < 0)
        goto err;

    /*Retrieve integer (4 bytes) from buffer*/
    memcpy (commandLength, buffer, sizeof(uint32_t));
    /*Java sends the primitive integer using big-endian order (it is the same as network order)*/
    *commandLength = be32toh (*commandLength);


    /*2. COMMAND*/
    /*Reserving commandLength + 1 because of the string end character*/
    if ((command = (char *) malloc(*commandLength + 1)) == NULL) {
        syslog (LOG_ERR, "command malloc failed: %m");
        goto err;
    }

    bzero(command, ((*commandLength) + 1));
    len = *commandLength;
    /*Wait max 2 seconds for data coming from client, otherwise exits with error.*/
    if (receive_from_socket (socket, command, len, timeout, utimeout) < 0)
        goto err;


    /*3. RESULTS*/	
    pre_fork_system(socket, command);


    /*4. CLOSE CONNECTION AND FINISH*/

err:
    free(command);
    closeSafely(socket);
    free(commandLength);

    pthread_exit(0);
}



int required_sock_options (int socket)
{
    int optval, flags;

    /*We want non blocking sockets.*/
    /*See the discussion of spurious readiness notifications under the BUGS section of select(2) */
    if ((flags = TEMP_FAILURE_RETRY(fcntl(socket,F_GETFL,0))) < 0) {
        syslog (LOG_ERR, "read socket status flags failed: %m");
        return -1;
    }

    if (TEMP_FAILURE_RETRY(fcntl(socket, F_SETFL, O_NONBLOCK|flags)) < 0){
        syslog (LOG_ERR, "set socket status flags failed: %m");
        return -1;
    }

    /*Portable programs should not rely on inheritance or noninheritance of file status flags and */
    /*always explicitly set all required flags*/
    optval = 1;
    if (setsockopt(socket, SOL_SOCKET, SO_REUSEADDR, &optval, sizeof(optval)) < 0) {
        syslog (LOG_ERR, "setsockopt SO_REUSEADDR failed: %m");
        return -1;
    }

    /*Enable keepalive for this socket*/
    optval = 1;
    if (setsockopt(socket, SOL_SOCKET, SO_KEEPALIVE, &optval, sizeof(optval)) < 0) {
        syslog (LOG_ERR, "setsockopt SO_KEEPALIVE failed: %m");
        return -1;
    }

    /*TODO: keepalive is not enough to find out if the connection is broken                     */
    /*      apparently it just works while the handshake phase. See:                            */
    /*      http://stackoverflow.com/questions/4345415/socket-detect-connection-is-lost         */
    /*      I have to implement an echo/ping messages protocol (application layer)              */

    return 0;
}



int readable_timeout (int fd, long timeout, long utimeout)
{
    struct timeval ptime;   /*Timeout, secs and usecs*/
    fd_set fd_read;         /*Values for select function.*/

    ptime.tv_sec = timeout;
    ptime.tv_usec = utimeout;
    FD_ZERO(&fd_read);
    FD_SET(fd, &fd_read);
  
    return TEMP_FAILURE_RETRY(select(fd+1, &fd_read, NULL, NULL, &ptime)); 
}



int receive_from_socket (int socket, char *data, int len, long timeout, long utimeout)
{
    int nData, iPos;	/*Control variables.*/
    int ret;            /*Store return value from select.*/

    nData = iPos = 0;
    do {
        ret = readable_timeout(socket, timeout, utimeout);

        if (ret == 0) {
            syslog(LOG_INFO, "receiving timeout error");
            return -1;
        } else if (ret == -1) {
            syslog(LOG_ERR, "receiving error: %m");
            return -1;
        }

        nData = TEMP_FAILURE_RETRY(recv(socket, &data[iPos], len, 0));

        if (nData < 0) {
            if ((errno != EAGAIN) && (errno != EWOULDBLOCK)) {
                syslog (LOG_ERR, "read TCP socket failed: %m");
                return -1;
            } else {
                /*see spurious readiness man select(2) BUGS section*/
                nData = 0;
                syslog (LOG_INFO, "read TCP socket spurious readiness");
            }
        } else if (nData == 0) {
            /*if nData is 0, client closed connection but we wanted to receive more data, this is an error */
            syslog (LOG_ERR, "expected more data from client");
            return -1;
        }

        len -= nData;
        iPos += nData;
    } while (len > 0);

    return 0;
}


int pre_fork_system(int socket, char *command)
{
    /*Required variables in order to share memory between processes*/
    key_t keyvalue;
    int idreturnstatus = -1;
    /*Store the return status from the process launched using system or execve*/
    /*Using shared memory between the child and parent process*/
    int *returnstatus = NULL;
	
    /*Required variables in order to share the semaphore between processes*/
    key_t keysemaphore;
    int idsemaphore = -1;
    sem_t *semaphore = NULL;    /*Used as a barrier: the child process just can start after sending the XML init code*/

    int returnValue = -1;       /*Return value from this function can be caught by upper layers, NOK by default*/
		
	
	
    /*Allocate shared memory because we can not use named semaphores*/
    /*We are using this semaphore as a barrier, because we just want to start the child process when the parent process has sent*/
    /*the XML header (see: for_system function)*/
	
    keysemaphore=ftok("/bin/ls", SHAREMEMSEM);  /*the /bin/ls must exist otherwise this does not work... */
    if (keysemaphore == -1) {
        syslog (LOG_ERR, "ftok failed: %m");
        goto end;
    }

    /*Attach shared memory*/
    if ((idsemaphore = shmget(keysemaphore,sizeof(sem_t), 0660 | IPC_CREAT)) < 0) {
        syslog (LOG_ERR, "semaphore initialization failed: %m");
        goto end_release_sem;
    }

    if ((semaphore = (sem_t *)shmat(idsemaphore, (void *)0, 0)) < 0) {
        goto end_release_sem;
    }

    if (sem_init(semaphore, 1, 1) < 0) {
        syslog (LOG_ERR, "semaphore initialization failed: %m");
        goto end_destroy_sem;
    }

    if (TEMP_FAILURE_RETRY(sem_wait(semaphore)) < 0) {
        syslog (LOG_ERR, "semaphore wait failed: %m");
        goto end_destroy_sem;
    }
	
	
	
    /*Allocate shared memory for the return status code from the process which is going to be launched by the system function.*/
    /*We want to share the returnstatus variable between this process and the child that is going to be created in the fork_system method.*/
    /*The goal is to store in this variable the return status code received from the process launched with the system method by the child process,*/
    /*then the parent process can retrieve that return status code and send it by TCP to the Java client.*/
    /*There are not concurrency issues because the parent process will just try to read this variable when the child process is dead, taking*/
    /*in that moment its last value and sending it to the Java client.*/


    keyvalue=ftok("/bin/ls", SHAREMEMKEY);  /*the /bin/ls must exist otherwise this does not work... */
    if (keyvalue == -1) {
        syslog (LOG_ERR, "ftok failed: %m");
        goto end_destroy_sem;
    }

    /*Attach shared memory*/
    if ((idreturnstatus=shmget(keyvalue,sizeof(int), 0660 | IPC_CREAT)) < 0) {
        syslog (LOG_ERR, "shmget failed: %m");
        goto end_release_mem;
    }

    returnstatus = (int *)shmat(idreturnstatus, (void *)0, 0);
    if ((*returnstatus)== -1) {
        syslog (LOG_ERR, "shmat failed: %m");
        goto end_release_mem;
    } 

    /*After allocating and attaching shared memory we reach this code if everything went OK.*/

    returnValue = fork_system(socket, command, semaphore, returnstatus);


end_release_mem:
    if (returnstatus != NULL) {
        /*detach memory*/
        if (shmdt ((int *)returnstatus) < 0)
            syslog (LOG_ERR, "returnstatus shared variable shmdt failed: %m");
    }

    /*Mark the segment to be destroyed.*/
    if (shmctl (idreturnstatus, IPC_RMID, (struct shmid_ds *)NULL) < 0 )
        syslog (LOG_ERR, "returnstatus shared variable shmctl failed: %m");
end_destroy_sem:
    if (sem_destroy(semaphore) <0)
         syslog (LOG_ERR, "semaphore destroy failed: %m");
end_release_sem:
    /*after sem_destroy-> input/output parameter NULL?*/
    if (semaphore != NULL) {
        /*detach memory*/
        if (shmdt ((sem_t *)semaphore) < 0)
            syslog (LOG_ERR, "semaphore shmdt failed: %m");
    }

    /*Mark the segment to be destroyed.*/
    if (shmctl (idsemaphore, IPC_RMID, (struct shmid_ds *)NULL) < 0 )
        syslog (LOG_ERR, "semaphore shmctl failed: %m");
end:
    return returnValue;
}



int fork_system(int socket, char *command, sem_t *semaphore, int *returnstatus) 
{
    int pid;
    int out[2], err[2];     /*Store pipes file descriptors. Write ends attached to the stdout and stderr streams.*/
    char buf[2000];         /*Read data buffer.*/
    char string[100];
    /*We are going to use a poll in order to find out if there are data coming from the*/
    /*pipes attached to the stdout and stderr streams.*/
    struct pollfd polls[2];
    int n;
    int childreturnstatus;
    int returnValue = 0;    /*return value from this function can be caught by upper layers, OK by default*/


    /*Value by default*/
    (*returnstatus) = 0;

	
    out[0] = out[1] = err[0] = err[1]  = -1;


    /*Creating the pipes, they will be attached to the stderr and stdout streams*/	
    if (pipe(out) < 0 || pipe(err) < 0) {
        syslog (LOG_ERR, "pipe failed: %m");
        goto err;
    }

    if ((pid=fork()) == -1) {
        syslog (LOG_ERR, "fork failed: %m");
        goto err;
    }

    if (pid == 0) {
        /*Child process*/
        /*It has to launch another one using system or execve*/
        if ((TEMP_FAILURE_RETRY(dup2(out[1],1)) < 0) || (TEMP_FAILURE_RETRY(dup2(err[1],2)) < 0)) {	
            syslog (LOG_ERR, "child dup2 failed: %m");
            /*Going to zombie state, hopefully waitpid will catch it*/	
            exit(-1);
        }

        if (TEMP_FAILURE_RETRY(sem_wait(semaphore)) < 0) {
            syslog (LOG_ERR, "child semaphore wait failed: %m");
            /*Going to zombie state, hopefully waitpid will catch it*/
            exit(-1);
        }

        *returnstatus=system(command);
        if (WIFEXITED(returnstatus) == 1)
            (*returnstatus) = WEXITSTATUS(*returnstatus);
        else
            (*returnstatus) = -1;
        /*Going to zombie state, hopefully waitpid will catch it*/
        exit(0);
    }
    else {
        /*Parent process*/
        /*It sends data to the Java client using a TCP connection.*/
        polls[0].fd=out[0];
        polls[1].fd=err[0];
        polls[0].events=POLLIN;
        polls[1].events=POLLIN;
        sprintf(string,"<?xml version=\"1.0\"?>");
        send(socket,string,strlen(string),0);
        sprintf(string,"<streams>");
        send(socket,string,strlen(string),0);

        /*Releasing barrier, the child process can keep running*/
        if (sem_post(semaphore) < 0 ) {
            syslog (LOG_ERR, "parent error releasing barrier: %m");
            /*TODO: May I kill a child process if it is already dead? I mean,   */
            /*      what could happen if the child process is dead?             */
            /*      Should I implement a SIGCHILD handler?                      */
            /*TODO: Should I have a SIGTERM handler in the child process?       */
            kill(pid, SIGTERM);
            goto err;
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
                    /*TODO: May I kill a child process if it is already dead? I mean,   */
                    /*      what could happen if the child process is dead?             */
                    /*      Should I implement a SIGCHILD handler?                      */
                    /*TODO: Should I have a SIGTERM handler in the child process?       */
                    kill(pid, SIGTERM);
                    /*I want to send an error status to the remote calling process                              */
                    /*TODO: Before giving this value I should make sure the child process is dead               */
                    /*      otherwise I could finish having in *returnstatus the value from the child process   */
                    (*returnstatus) = -1;
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
                        (*returnstatus) = -1;
                    break;
                }
                /*The child process is not dead, keep polling more data from stdout or stderr streams*/
            }
        }
    }
    /*Reaching this code when child finished or if error while polling pipes*/
    sprintf(string,"<ret><![CDATA[%d]]></ret>", (*returnstatus));
    send(socket,string,strlen(string),0);
    sprintf(string,"</streams>");
    send(socket,string,strlen(string),0);

    /*Stuff just done by the Parent process. The child process ends with exit*/

end:
    closeSafely (out[0]);
    closeSafely (out[1]);
    closeSafely (err[0]);
    closeSafely (err[1]);
    return returnValue;
err:
    returnValue = -1;
	goto end;
}


void sigint_handler(int sig)
{
    if (daemonPID != getpid()) {
        //Do nothing
        return;
    }

    if (signal (SIGINT, SIG_IGN) == SIG_ERR)
        syslog (LOG_ERR, "signal SIGINT desactivation failed: %m");

    closeSafely (sockfd);
    /*TODO: kill child processes and release allocate memory*/
    exit (0);
}

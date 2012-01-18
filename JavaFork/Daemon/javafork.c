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
#include "javafork.h"


int main (int argc, char *argv[]) {
    pid_t pid;

	if ((pid = fork()) == -1) {
		perror("fork: ");
    	return -1;
	}
	else {
		if (pid == 0) {
			/*child process*/
			main_child(argc, argv);
		}
	}

	return 0;
}


int main_child (int argc, char *argv[]) {
	struct protoent *protocol;
	struct sockaddr_in addr_server;
	struct sockaddr_in  addr_client;
	int sockfd;
    int sockclient;
    pthread_t idThread;
	int clilen;

	
	daemonize(argv[0], LOG_SYSLOG, LOG_PID);
    setsid();

	signal(SIGPIPE,SIG_IGN);

	/*Retrieve protocol number from /etc/protocols file */
    protocol=getprotobyname("tcp");
	if (protocol == NULL) {
		syslog(LOG_ERR, "cannot map \"tcp\" to protocol number");
    	exit (1);
	}

	bzero((char*) &addr_server, sizeof(addr_server));
	addr_server.sin_family = AF_INET;
    if (inet_pton(AF_INET, argv[1], &addr_server.sin_addr.s_addr) <= 0)
        syslog (LOG_ERR, "error inet_pton");

	addr_server.sin_port = htons(atol(argv[2]));

	if ((sockfd = socket(AF_INET, SOCK_STREAM, 0)) < 0) {
        syslog (LOG_ERR, "socket creation failed");
		exit(1);
	}

	if (bind(sockfd, (struct sockaddr *) &addr_server, sizeof(addr_server)) < 0) {
		syslog (LOG_ERR, "socket bind failed");
        exit(1);
	}

	if (listen (sockfd, atoi(argv[3])) < 0 ) {
		syslog (LOG_ERR, "socket listen failed");
        exit(1);
	}	

	while(1) {
		clilen =  sizeof(addr_client);
		if ((sockclient = accept (sockfd, (struct sockaddr *) &addr_client, &clilen)) < 0) {
			syslog (LOG_ERR, "socket accept failed");
        	exit(1);
		}
		if (pthread_create (&idThread, NULL, serverThread, (void *)sockclient) != 0 ) {
			syslog (LOG_ERR, "thread creation failed");
		}	
	}
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

  	ioctl(fd, TIOCNOTTY, (caddr_t)0);
  	close(fd);

  	if((fd = open( "/dev/null", O_RDWR, 0) ) == -1) {
    	close(fd);
    	return 0;
  	}
  	dup2(fd,0);
  	dup2(fd,1);
  	dup2(fd,2);
  	close(fd);

  	return 0;
}

void *serverThread (void * arg) {
	int sockclient;
    int n;
    char buf[512];

	sockclient = (int) arg;
	pthread_detach(pthread_self());

	n = recv(sockclient, buf, sizeof(buf), 0);
	if(n > 0){
    	buf[n] = '\0';
    	fork_system(sockclient, buf);
  	}

	close(sockclient);
	
	pthread_exit(0);
}


int fork_system(int socket, char *command) {
	int pid;
    int pfdOUT[2];
    int pfdERR[2];
	char buf[2000];
    char string[100];
	struct pollfd polls[2];
	int returnstatus;
	int n;

	sem_t * semaphore;
	/*Required variables in order to share memory between processes*/
	key_t keyvalue;
	int idreturnst;
	int *returnst;

	if (pipe(pfdOUT) == -1) {
		syslog (LOG_ERR, "pipe for stdout failed");
		close(pfdOUT[0]);
        close(pfdOUT[1]);
    	return -1;
  	}
  	if (pipe(pfdERR) == -1) {
		syslog (LOG_ERR, "pipe for stderr failed");
    	close(pfdOUT[0]);
    	close(pfdOUT[1]);
		close(pfdERR[0]);
        close(pfdERR[1]);
    	return -1;
  	}

	if ((semaphore = sem_open("javaforksem", O_CREAT, 0644, 1)) == SEM_FAILED) {
		syslog (LOG_ERR, "semaphore open failed");
        close(pfdOUT[0]);
        close(pfdOUT[1]);
        close(pfdERR[0]);
        close(pfdERR[1]);
		if (sem_close(semaphore) <0 ) {
			syslog (LOG_ERR, "semaphore open failed and close failed");
		}
		if (sem_unlink("javaforksem") < 0) {
			syslog (LOG_ERR, "semaphore open failed and unlink failed");
		}
		return -1;
	}

	if (sem_init(semaphore, 1, 1) < 0) {
		syslog (LOG_ERR, "semaphore initialization failed");
		close(pfdOUT[0]);
        close(pfdOUT[1]);
        close(pfdERR[0]);
        close(pfdERR[1]);
        if (sem_close(semaphore) <0 ) {
            syslog (LOG_ERR, "semaphore open failed and close failed");
        }
        if (sem_unlink("javaforksem") < 0) {
            syslog (LOG_ERR, "semaphore open failed and unlink failed");
        }
    	return -1;
  	}
	
	if (sem_wait(semaphore) < 0) {
		syslog (LOG_ERR, "semaphore wait failed");
	}

	/*Allocate shared memory*/
    keyvalue=ftok("/bin/ls", SHAREMEMKEY);  /*the /bin/ls must exist otherwise this does not work... */
	if (keyvalue == -1) {
		syslog (LOG_ERR, "ftok failed");
		/*TODO: Close again pipes and semaphore.*/
		return -1;
	}
	/*Attach shared memory*/
	idreturnst=shmget(keyvalue,sizeof(int), 0660 | IPC_CREAT);
    if (idreturnst == -1) {
		syslog (LOG_ERR, "shmget failed");
		/*TODO: Close again pipes and semaphore.*/
		return -1;
	}
	returnst = (int *)shmat(idreturnst, (void *)0, 0);
	if ((*returnst)== -1) {
		syslog (LOG_ERR, "shmat failed");
		/*TODO: Close again pipes and semaphore.*/
		return -1;
	}

	/*By default*/
	(*returnst) = 0;

	if ((pid=fork()) == -1) {
    	perror("fork: ");
		close(pfdOUT[0]);
        close(pfdOUT[1]);
        close(pfdERR[0]);
        close(pfdERR[1]);
		if (sem_close(semaphore) <0 ) {
            syslog (LOG_ERR, "semaphore open failed and close failed");
        }
        if (sem_unlink("javaforksem") < 0) {
            syslog (LOG_ERR, "semaphore open failed and unlink failed");
        }
    	return -1;
  	}
	else {
		/*Child process*/
		if (pid == 0) {
			if (dup2(pfdOUT[1],1) < 0) {	
				syslog (LOG_ERR, "child: dup2 pfdOUT failed");	
				exit(-1);
			}
			if (dup2(pfdERR[1],2) < 0) {    
                syslog (LOG_ERR, "child: dup2 pfdERR failed");
				exit(-1);
            }
			if (sem_wait(semaphore) < 0) {
        		syslog (LOG_ERR, "child: semaphore wait failed");
		    }
			returnstatus=system(command);
			if (WIFEXITED(returnstatus) == 1) {
				(*returnst) = WEXITSTATUS(returnstatus);
			}
			else {
				(*returnst) = -1;
			}
			exit(0);
		}
		else {
			/*Parent process*/
			polls[0].fd=pfdOUT[0];
      		polls[1].fd=pfdERR[0];
      		polls[0].events=POLLIN;
      		polls[1].events=POLLIN;
      		sprintf(string,"<?xml version=\"1.0\"?>");
      		send(socket,string,strlen(string),0);
      		sprintf(string,"<salida>");
      		send(socket,string,strlen(string),0);
			/*The child will be woken up*/
			if (sem_post(semaphore) < 0 ) {
				syslog (LOG_ERR, "error waiking up child process");
				/*TODO: exit closing the descriptors*/
			}
			while(1){
				if(poll(polls,2,100)){
          			if(polls[0].revents&&POLLIN) {
            			bzero(buf,2000);
						n=read(pfdOUT[0],buf,1990);
						sprintf(string,"<out><![CDATA[");
            			send(socket,string,strlen(string),0);
        				send(socket,buf,n,0);
            			sprintf(string,"]]></out>");
        				send(socket,string,strlen(string),0);
      				} 
					if(polls[1].revents&&POLLIN) {
						bzero(buf,2000);
            			n=read(pfdERR[0],buf,1990);
						sprintf(string,"<error><![CDATA[");
			            send(socket,string,strlen(string),0);
            			send(socket,buf,n,0);
            			sprintf(string,"]]></error>");
            			send(socket,string,strlen(string),0);
          			}
					if(!polls[0].revents&&POLLIN && !polls[1].revents&&POLLIN) {
						syslog (LOG_ERR, "Error polling pipes");
        				break;
          			}
				}
				else {
					/*When timeout*/
					if(waitpid(pid,NULL,WNOHANG)) {
						/*Child is dead, we can finish the connection*/
						break;
					}
					/*The child process is not dead, keep polling more data from stdout or stderr streams*/
				}
			}
			
			/*We reach this code when the child process is dead or because of an error polling pipes*/
			/*In the second case the result stored in *returnst could be wrong, anyway there was*/
			/*an error so, the result is unpredictable.*/
			/*TODO: if error while polling pipes do not reach this code an exit with -1*/
			sprintf(string,"<ret><![CDATA[%d]]></ret>", (*returnst));
        	send(socket,string,strlen(string),0);
        	sprintf(string,"</salida>");
    		send(socket,string,strlen(string),0);

			close(pfdOUT[0]);
  			close(pfdOUT[1]);
  			close(pfdERR[0]);
  			close(pfdERR[1]);
			if (sem_close(semaphore) <0 ) {
            	syslog (LOG_ERR, "semaphore close failed");
        	}
        	if (sem_unlink("javaforksem") < 0) {
            	syslog (LOG_ERR, "semaphore unlink failed");
        	}
	
			shmdt ((int *)returnst);
    		shmctl (idreturnst, IPC_RMID, (struct shmid_ds *)NULL);
		}
	}

	return 0;
}

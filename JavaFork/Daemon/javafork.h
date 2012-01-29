/*System V IPC keys*/
#define SHAREMEMKEY 1
#define SHAREMEMSEM 2

/*Non-argument default values*/
#define PORT 5193
#define IPADDRESS "127.0.0.1"
#define QUEUE 6



/****************************************************************************************
* This method is used by pthread_create                                                 *
* 																					    *
* INPUT PARAMETER: socket file descriptor												*
* RETURNS: void 																			*
****************************************************************************************/
void *serverThread (void *arg);



/****************************************************************************************
* This method is used by pthread_create                                                 *
*                                                                                       *
* INPUT PARAMETER: socket file descriptor                                               *
* INPUT PARAMETER: 
* INPUT PARAMETER:
* RETURNS: void                                                                          *
****************************************************************************************/
int daemonize(const char *pname, int facility, int option);



/****************************************************************************************
* This method is used by pthread_create                                                 *
*                                                                                       *
* INPUT PARAMETER: socket file descriptor                                               *
* RETURNS: int	                                                                        *
****************************************************************************************/
int main_daemon (char *address, int port, int queue);



/****************************************************************************************
* This method is used by pthread_create                                                 *
*                                                                                       *
* INPUT PARAMETER: socket file descriptor                                               *
* RETURNS: void                                                                          *
****************************************************************************************/
int fork_system(int socket, char *command, sem_t *semaphore, int *returnst);


/****************************************************************************************
* This method is used by pthread_create                                                 *
*                                                                                       *
* INPUT PARAMETER: socket file descriptor                                               *
* RETURNS: void                                                                          *
****************************************************************************************/
int pre_fork_system(int socket, char *command);





/****************************************************************************************
* This method is used by pthread_create                                                 *
*                                                                                       *
* INPUT PARAMETER: socket file descriptor                                               *
* RETURNS: void                                                                          *
****************************************************************************************/
void sigint_handler();



int required_sock_options (int socket);
int receive_from_socket (int socket, char *data, int len, long timeout, long utimeout);
int readable_timeout (int fd, long timeout, long utimeout);

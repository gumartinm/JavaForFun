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
int main_child (char *address, int port, int queue);



/****************************************************************************************
* This method is used by pthread_create                                                 *
*                                                                                       *
* INPUT PARAMETER: socket file descriptor                                               *
* RETURNS: void                                                                          *
****************************************************************************************/
int fork_system(int socket, char *command);




/****************************************************************************************
* This method is used by pthread_create                                                 *
*                                                                                       *
* INPUT PARAMETER: socket file descriptor                                               *
* RETURNS: void                                                                          *
****************************************************************************************/
void sigterm_handler();

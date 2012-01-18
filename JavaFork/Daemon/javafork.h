/*System V IPC key*/
#define SHAREMEMKEY 1

void *serverThread (void *arg);
int daemonize(const char *pname, int facility, int option);
int main_child (int argc, char *argv[]);
int fork_system(int socket, char *command);

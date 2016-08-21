function Controller($rootScope, USERS) {
    var vm = this;

    $rootScope.$on(USERS.ROOTSCOPE.BROADCAST, usersChildOnRootBroadcast); // Noncompliant {{Do not use $rootScope.$on because it leaks the Controller instance.}}
        
    function NestedFunction() {
        $rootScope  // Noncompliant {{Do not use $rootScope.$on because it leaks the Controller instance.}}
        
        .$on(USERS.ROOTSCOPE.BROADCAST, usersChildOnRootBroadcast);
    }
    
    function usersChildOnRootBroadcast(events, broadcastUser) {
        vm.broadcastUser = broadcastUser;
    }
}

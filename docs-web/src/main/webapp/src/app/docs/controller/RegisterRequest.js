'use strict';

/**
 * Register request controller.
 */
angular.module('docs').controller('RegisterRequest', function($scope, Restangular, $translate, $dialog) {
    $scope.request = {};

    $scope.submit = function() {
        Restangular.one('user').post('register_request', {
            username: $scope.request.username,
            email: $scope.request.email
        }).then(function() {
            var title = $translate.instant('register_request.success_title');
            var msg = $translate.instant('register_request.success_message');
            var btns = [{ result: 'ok', label: $translate.instant('ok'), cssClass: 'btn-primary' }];
            $dialog.messageBox(title, msg, btns, function() {
                $scope.$close();
            });
        }, function(response) {
            var title = $translate.instant('register_request.error_title');
            var msg;
            if (response.data.type === 'AlreadyExistingUsername') {
                msg = $translate.instant('register_request.error_username_exists');
            } else if (response.data.type === 'AlreadyExistingEmail') {
                msg = $translate.instant('register_request.error_email_exists');
            } else {
                msg = $translate.instant('register_request.error_message');
            }
            var btns = [{ result: 'ok', label: $translate.instant('ok'), cssClass: 'btn-primary' }];
            $dialog.messageBox(title, msg, btns);
        });
    };

    $scope.cancel = function() {
        $scope.$close();
    };
});
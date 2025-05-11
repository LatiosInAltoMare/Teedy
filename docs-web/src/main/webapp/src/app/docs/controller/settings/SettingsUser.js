'use strict';

/**
 * Settings user page controller.
 */
angular.module('docs').controller('SettingsUser', function($scope, $state, Restangular, $translate, $dialog) {
  /**
   * Load users and pending requests from server.
   */
  $scope.loadData = function() {
    // Load existing users
    Restangular.one('user/list').get({
      sort_column: 1,
      asc: true
    }).then(function(data) {
      $scope.users = data.users;
    });

    // Load pending registration requests
    Restangular.one('user/register_request/list').get().then(function(data) {
      $scope.requests = data.requests;
      // Initialize password and storage_quota for each request
      angular.forEach($scope.requests, function(request) {
        request.password = '';
        request.storage_quota = 0;
      });
    });
  };

  $scope.loadData();

  /**
   * Edit a user.
   */
  $scope.editUser = function(user) {
    $state.go('settings.user.edit', { username: user.username });
  };

  /**
   * Approve a registration request.
   */
  $scope.approveRequest = function(request) {
    if (!request.password || request.storage_quota === undefined) {
      var title = $translate.instant('settings.user.approve_error_title');
      var msg = $translate.instant('settings.user.approve_error_message');
      var btns = [{ result: 'ok', label: $translate.instant('ok'), cssClass: 'btn-primary' }];
      $dialog.messageBox(title, msg, btns);
      return;
    }

    var storageQuotaBytes = request.storage_quota * 1000000; // Convert MB to bytes

    Restangular.one('user/register_request/' + request.id + '/approve').post('', {
      password: request.password,
      storage_quota: storageQuotaBytes
    }).then(function() {
      $scope.loadData(); // Reload data to reflect changes
      var title = $translate.instant('settings.user.approve_success_title');
      var msg = $translate.instant('settings.user.approve_success_message');
      var btns = [{ result: 'ok', label: $translate.instant('ok'), cssClass: 'btn-primary' }];
      $dialog.messageBox(title, msg, btns);
    }, function(response) {
      var title = $translate.instant('settings.user.approve_error_title');
      var msg = $translate.instant('settings.user.approve_error_message');
      if (response.data.type === 'AlreadyExistingUsername') {
        msg = $translate.instant('settings.user.approve_error_username_exists');
      }
      var btns = [{ result: 'ok', label: $translate.instant('ok'), cssClass: 'btn-primary' }];
      $dialog.messageBox(title, msg, btns);
    });
  };

  /**
   * Reject a registration request.
   */
  $scope.rejectRequest = function(request) {
    var title = $translate.instant('settings.user.reject_confirm_title');
    var msg = $translate.instant('settings.user.reject_confirm_message');
    var btns = [
      { result: 'cancel', label: $translate.instant('cancel') },
      { result: 'ok', label: $translate.instant('ok'), cssClass: 'btn-primary' }
    ];

    $dialog.messageBox(title, msg, btns, function(result) {
      if (result === 'ok') {
        Restangular.one('user/register_request/' + request.id + '/reject').post('').then(function() {
          $scope.loadData(); // Reload data to reflect changes
        });
      }
    });
  };
});
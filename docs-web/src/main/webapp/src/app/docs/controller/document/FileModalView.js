angular.module('docs').controller('FileModalView', function ($uibModalInstance, $scope, $state, $stateParams, $sce, Restangular, $transitions, $translate, $dialog) {
  // 初始化翻译状态
  $scope.isTranslated = false;

  var setFile = function (files) {
    // 搜索当前文件
    _.each(files, function (value) {
      if (value.id === $stateParams.fileId) {
        $scope.file = value;
        $scope.trustedFileUrl = $sce.trustAsResourceUrl('../api/file/' + $stateParams.fileId + '/data');
      }
    });
  };

  // 加载文件
  Restangular.one('file/list').get({ id: $stateParams.id }).then(function (data) {
    $scope.files = data.files;
    setFile(data.files);

    // 文件未找到，可能是版本
    if (!$scope.file) {
      Restangular.one('file/' + $stateParams.fileId + '/versions').get().then(function (data) {
        setFile(data.files);
      });
    }
  });

  // 翻译文件
  $scope.translateFile = function() {
    $scope.isTranslating = true; // 显示加载状态

    Restangular.one('file/' + $stateParams.fileId + '/translate').post().then(function(response) {
      $scope.isTranslating = false;
      $scope.isTranslated = true; // 标记为已翻译

      // 替换 trustedFileUrl 为翻译文件的 URL
      $scope.trustedFileUrl = $sce.trustAsResourceUrl(response.translatedUrl);

      // 设置 mimetype 为 PDF（假设翻译文件为 PDF）
      $scope.file.mimetype = 'application/pdf';

      // 显示成功提示
      var title = $translate.instant("Translate Successfully");
      var msg = $translate.instant("The file has been translated");
      var btns = [{ result: 'ok', label: $translate.instant('ok'), cssClass: 'btn-primary' }];
      $dialog.messageBox(title, msg, btns);
    }, function(error) {
      $scope.isTranslating = false;

      // 显示错误提示
      var title = $translate.instant('file.view.translate_error_title');
      var msg = $translate.instant('file.view.translate_error_message');
      var btns = [{ result: 'ok', label: $translate.instant('ok'), cssClass: 'btn-primary' }];
      $dialog.messageBox(title, msg, btns);
    });
  };

  // 重置翻译状态和 URL
  $scope.$on('$stateChangeSuccess', function() {
    $scope.isTranslated = false;
    $scope.trustedFileUrl = $sce.trustAsResourceUrl('../api/file/' + $stateParams.fileId + '/data');
  });

  // 其他现有函数（保持不变）
  $scope.nextFile = function () {
    var next = undefined;
    _.each($scope.files, function (value, key) {
      if (value.id === $stateParams.fileId) {
        next = $scope.files[key + 1];
      }
    });
    return next;
  };

  $scope.previousFile = function () {
    var previous = undefined;
    _.each($scope.files, function (value, key) {
      if (value.id === $stateParams.fileId) {
        previous = $scope.files[key - 1];
      }
    });
    return previous;
  };

  $scope.goNextFile = function () {
    var next = $scope.nextFile();
    if (next) {
      $state.go('^.file', { id: $stateParams.id, fileId: next.id });
    }
  };

  $scope.goPreviousFile = function () {
    var previous = $scope.previousFile();
    if (previous) {
      $state.go('^.file', { id: $stateParams.id, fileId: previous.id });
    }
  };

  $scope.openFile = function () {
    window.open('../api/file/' + $stateParams.fileId + '/data');
  };

  $scope.openFileContent = function () {
    window.open('../api/file/' + $stateParams.fileId + '/data?size=content');
  };

  $scope.printFile = function () {
    var popup = window.open('../api/file/' + $stateParams.fileId + '/data', '_blank');
    popup.onload = function () {
      popup.print();
      popup.close();
    };
  };

  $scope.closeFile = function () {
    $uibModalInstance.dismiss();
  };

  var off = $transitions.onStart({}, function(transition) {
    if (!$uibModalInstance.closed) {
      if (transition.to().name === $state.current.name) {
        $uibModalInstance.close();
      } else {
        $uibModalInstance.dismiss();
      }
    }
    off();
  });

  $scope.canDisplayPreview = function () {
    return $scope.file && $scope.file.mimetype !== 'application/pdf';
  };
});
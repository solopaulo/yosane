/**
 * 
 */
var yosaneServices = angular.module('yosaneServices', ['ngResource']);

yosaneServices.factory('restful', ['$resource',
  function($resource){
    return {
        getScanners : function(cb,errcb) {
            return $resource('/yosane/scanners', {}, {
              query: {method:'GET', params:{}, isArray:false}
            }).get({},cb, errcb);
        },
        scanImage : function(parameters,cb,errcb) {
            return $resource(parameters.url, {}, {
                fetch: {method:'POST',params: {  } }
              }).fetch({deviceOptions: parameters.options},cb,errcb);
        },
        emailImage : function() {
            return $resource('/yosane/send/email/image',{}, {
               send : { method:'POST' }
            });
        },
        emailPdf : function() {
            return $resource('/yosane/send/email/pdf',{}, {
               send : { method:'POST' }
            });
        },        
        copyImage : function() {
            return $resource('/yosane/send/localfile/image',{}, {
                send : { method:'POST' }
             });
        },
        getLocalFileConfig : function(cb,errcb) {
            return $resource('/yosane/send/localfile/config', {}, {
                query: {method:'GET', params:{}, isArray:false}
              }).get({},cb, errcb);
        },
        getEmailRecipients : function(cb,errcb) {
            return $resource('/yosane/send/email/config', {}, {
                query: {method:'GET', params:{}, isArray:false}
              }).get({},cb, errcb);
        },
        getSettings : function(url,cb,errcb) {
            return $resource(url, {}, {
                query: {method:'GET', params:{}, isArray:false}
              }).get({},cb, errcb);
        },
        getNotifications : function(cb,errcb) {
            return $resource('/yosane/notifications',{},{
               query : {method:'GET',params:{}} 
            }).get({},cb,errcb);
        }
    };
  }]);

yosaneServices.factory('imageService',[function() {
    return {
      images : [],
      clearImages : function() {
          images = [];
      },
      addImage : function(image) {
          this.images.push(image);
      }  
    };
}]);

yosaneServices.factory('scannerService',[function() {
    return {
        excludedSettingsGroups : ['threshold','gamma','buttons','extras',''],
        settingsGroups : [ ],
        selected : "",
        currentScanner : {},
        currentScannerSettings : [],
        changedSettings : [],
        
        updateSettings : function(options) {            
          this.currentScannerSettings = [];
          for (var i = 0; i < options.length; i++) {
              var group = options[i].group.toLowerCase() ;
              if ( this.excludedSettingsGroups.indexOf( group ) >= 0 ) {
                  continue;
              }
              this.currentScannerSettings.unshift( options[i] );
              if ( this.settingsGroups.indexOf( group ) < 0 ) {
                  this.settingsGroups.unshift( group );
              }
          }
        },
        settingChanged : function(setting) {
          if ( this.changedSettings.indexOf(setting) >= 0 ) {
              return;
          }
          this.changedSettings.unshift(setting)  
        },
        clearAllSettings : function() {
          this.changedSettings = [];
          this.currentScannerSettings = [];
          this.settingsGroups = [];
        },
        buildDeviceOptions : function() {
          var arr = [];  
          for (var i = 0; i < this.changedSettings.length; i++) {
              var changedOpt = this.changedSettings[i];
              for (var j = 0; j < this.currentScannerSettings.length; j++) {
                  var opt = this.currentScannerSettings[j];
                  if ( opt.name != changedOpt ) {
                      continue;
                  }
                  arr.unshift( { name : changedOpt, value : opt.value});
              }
          }
          return arr;
        },
        defaultImage : 'yosane/assets/images/180x240.gif',
        scannedImage : 'yosane/assets/images/180x240.gif',
        scanners : [],
        settings : [],
        status : "",
        emptyScannerList : [ { name : "", title : "No scanners available"}]
    };
}]);

yosaneServices.factory('notificationService',['restful',function(restful) {
    return {
      notifications : [] ,
      newNotifications : [],
      
      flush : function() {
          var t = this;
         this.newNotifications = this.newNotifications.filter( function(notification) {
             t.notifications.unshift( notification );
            return false; 
         });
         return this.notifications;
      },
      
      getNotifications : function() {
          return this.notifications;
      },
      
      themeNotification : function(nType) {
        if ( nType === undefined || nType.length == 0 ) {
            return 'alert-default';
        } else if ( nType.indexOf('ERROR') == 0 ) {
            return 'alert-danger';
        } else if ( nType.indexOf('COMPLETED') == 0 ) {
            return 'alert-success';
        } else if ( nType.indexOf('INFO') == 0 ) {
            return 'alert-info';
        }
        return 'alert-default';
      },
      
      poll : function() {
          var t = this;
          restful.getNotifications( function(data) {
              if ( data != undefined && data.notifications != undefined && data.notifications.length > 0 ) {
                  for (var i = 0; i < data.notifications.length; i++) {
                      t.newNotifications.unshift( data.notifications[i] );
                  }                  
              }
          },function() {
             console.log("Failed to retrieve notifications.");
          });
      }      
    };
}]);

yosaneServices.factory('configService',['restful',function(restful) {
    return {
      localPaths : [],
      recipients: [],
      
      updateLocalPaths : function() {
          var t = this;
          this.localPaths = [];
          restful.getLocalFileConfig(function(resp) {
              var data = resp.paths;
              for (f in data) {
                  t.localPaths.unshift( { name: f, path : data[f]})
              }
          })
      },
      updateRecipients : function() {
          var t = this;
          this.recipients = [];
          restful.getEmailRecipients(function(resp) {
              if ( resp.recipients !== undefined && resp.recipients.length > 0 ) {
                  t.recipients = resp.recipients;
              }
          })
      }
    };
}]);
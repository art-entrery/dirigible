/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company and Eclipse Dirigible contributors
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v20.html
 *
 * SPDX-FileCopyrightText: 2021 SAP SE or an SAP affiliate company and Eclipse Dirigible contributors
 * SPDX-License-Identifier: EPL-2.0
 */
(function(angular){
"use strict";

	angular.module('discussion-boards')
	.factory('httpRequestInterceptor', function () {
		var csrfToken = null;
		return {
			request: function (config) {
				config.headers['X-Requested-With'] = 'Fetch';
				config.headers['X-CSRF-Token'] = csrfToken ? csrfToken : 'Fetch';
				return config;
			},
			response: function(response) {
				var token = response.headers()['x-csrf-token'];
				if (token) {
					csrfToken = token;
				}
				return response;
			}
		};
	})
	.config(['$httpProvider', function($httpProvider) {
		$httpProvider.interceptors.push('httpRequestInterceptor');
	}])
	.service('SecureBoard', ['$resource', '$log', function($resource, $log) {
	  	return $resource('../../../js/ide-discussions/svc/user/board.js/:boardId', { boardId:'@id' }, {
			    save: {
			        method: 'POST',
			        interceptor: {
		                response: function(res) {
		                	var location = res.headers('Location');
		                	if(location){
		                		var id = location.substring(location.lastIndexOf('/')+1);
		                		angular.extend(res, { "id": id });
	                		} else {
	                			$log.error('Cannot infer id after save operation. HTTP Response Header "Location" is missing: ' + location);
	            			}
	                        return res;
		                }
		            }, 
		            isArray: false
			    },
			    update: {
			        method: 'PUT'
			    }
			});
	}])
	.service('SecureBoardVote', ['$resource', function($resource) {
	  	return $resource('../../../js/ide-discussions/svc/user/board.js/:boardId/vote', {}, 
	  			{get: {method:'GET', params:{}, isArray:false, ignoreLoadingBar: true}},
	  			{save: {method:'POST', params:{}, isArray:false, ignoreLoadingBar: true}});
	}])		
	.service('SecureBoardTags', ['$resource', function($resource) {
	  	return $resource('../../../js/ide-discussions/svc/user/board.js/:boardId/tags', {}, 
	  			{
	  				get: {method:'GET', params:{}, isArray:true, ignoreLoadingBar: true},
	  				save: {method:'POST', params:{}, isArray:true, ignoreLoadingBar: true},
	  				remove: {method:'DELETE', params:{}, isArray:true, ignoreLoadingBar: true}
	  			});
	}])		
	.service('$SecureComment', ['$resource', '$log', function($resource, $log) {
	 	return $resource('../../../js/ide-discussions/svc/user/comment.js/:commentId', { commentId:'@id' }, {
			    save: {
			        method: 'POST',
			        interceptor: {
		                response: function(res) {
		                	var location = res.headers('Location');
		                	if(location){
		                		var id = location.substring(location.lastIndexOf('/')+1);
		                		angular.extend(res.resource, { "id": id });
	                		} else {
	                			$log.error('Cannot infer id after save operation. HTTP Response Header "Location" is missing: ' + location);
	            			}
	                        return res.resource;
		                }
		            }, 
		            isArray: false
			    },
			    update: {
			        method: 'PUT'
			    }
			});
	}])
	.service('$LoggedUser', ['$resource', function($resource) {
		var UserSvc =  $resource('../../../js/ide-discussions/svc/user/user.js', {}, //$resource('../../js/profile/user.js', {}, 
	  					{get: {method:'GET', params:{}, isArray:false, ignoreLoadingBar: true}});
	  	var get = function(){
		  	return UserSvc.get().$promise;
	  	};
	  	return {
	  		get: get
	  	};
	}])
	.service('$LoggedUserProfile', ['$resource', function($resource) {
		var UserSvc =  $resource('../../../js/ide-discussions/svc/user/profile.js/logout', {}, 
	  					{get: {method:'GET', params:{}, isArray:false, ignoreLoadingBar: true}});
	  	var logout = function(){
		  	return UserSvc.get().$promise;
	  	};
	  	return {
	  		logout: logout
	  	};
	}]);
})(angular);
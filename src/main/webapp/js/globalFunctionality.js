
dojo.provide("core.globalFunctionality");

//Create our widget!
dojo.declare('core.globalFunctionality', [dijit._Widget],{

    tabManager : null,

    getEventFromIdWithCallback : function(id, callback){
        var typeArgs = {
            url: "/api/Camera/" + id,
            handleAs: "json",
            load: function(data){
                callback(data);
            },
            error: function(error){
                callback(error);
            }
        }
        dojo.xhrGet(typeArgs);
    },

    getAllCameras : function(callback){
        if (callback == null | callback == undefined){
            var result;
            var typeArgs = {
                url: "/api/camera/",
                handleAs: "json",
                sync : true,
                load: dojo._base.lang.hitch(this, function(data){
                    result = data;
                }),
                error: function(error){
                    result = data;
                }
            }
            dojo._base.lang.hitch(this, dojo.xhrGet(typeArgs));
            return result;
        }
        else{
            var typeArgs = {
                url: "/api/Camera/",
                handleAs: "json",
                load: function(data){
                    callback(data);
                },
                error: function(error){
                    callback(data);
                }
            }
            dojo.xhrGet(typeArgs);
        }
    },

    getCameraFromIdWithCallback : function(id, callback){
        var typeArgs = {
            url: "/api/Camera/" + id,
            handleAs: "json",
            load: function(data){
                callback(data);
            },
            error: function(error){
                callback(error);
            }
        }
        dojo.xhrGet(typeArgs);
    },

    getEventFromId : function(id){
        var typeArgs = {
            url: "/api/Event/" + id,
            handleAs: "json",
            sync: true,
            load: function(data){
                return data;
            },
            error: function(error){
                return error;
            }
        }
        dojo.xhrGet(typeArgs);
    },

    getCameraFromId : function(id){
        var typeArgs = {
            url: "/api/Camera/" + id,
            handleAs: "json",
            sync : true,
            load: function(data){
                return data;
            },
            error: function(error){
                return error;
            }
        }
        dojo.xhrGet(typeArgs);
    }


})
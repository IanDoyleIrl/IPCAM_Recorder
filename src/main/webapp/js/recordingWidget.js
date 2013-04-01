
dojo.provide("core.recordingWidget");

//Create our widget!
dojo.declare('core.recordingWidget', [dijit._Widget, dijit._Templated],{
    widgetsInTemplate : true,
    templatePath: "js/htmlTemplates/recordingWidget.html",
    currentEventId : null,
    eventStats : null,
    recordingStats : null,
    cameras : null,
    _this : null,

    postCreate : function(){
        //alert("recording");
        _this = this;
        this.populateVariables();
    },

    populateVariables : function (){
        var cameraArgs = {
            url: "/api/camera",
            handleAs: "json",
            load: function(data){
                _this.cameras = data;
            },
            error: function(error){
                //alert(error);
            }
        }
        var eventArgs = {
            url: "/api/event/stats",
            handleAs: "json",
            load: function(data){
                _this.eventStats = data;
            },
            error: function(error){
                alert(error);
            }
        }
        var recordingArgs = {
            url: "/api/recording/stats",
            handleAs: "json",
            load: function(data){
                _this.recordingStats = data;
            },
            error: function(error){
                //alert(error);
            }
        }
        // Call the asynchronous xhrGet
        dojo.xhrGet(cameraArgs);
        dojo.xhrGet(eventArgs);
        dojo.xhrGet(recordingArgs);
    }


});
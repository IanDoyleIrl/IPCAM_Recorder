
dojo.provide("core.recordingWidget");

dojo.require("dijit.form.Select");
dojo.require("dijit.form.TextBox");
dojo.require("dojox.layout.TableContainer");
dojo.require("dijit._Widget");
dojo.require("dijit._Templated");
dojo.require("dojox.data.CsvStore");
dojo.require("dojox.grid.EnhancedGrid");
dojo.require("dojo.data.ItemFileReadStore");
dojo.require("dojox.layout.TableContainer");
dojo.require("dijit.form.TextBox");
dojo.require("dojox.socket"),
dojo.require("dijit.form.NumberSpinner");
dojo.require("dojox.grid.enhanced.plugins.NestedSorting");
dojo.require("dojox.grid.enhanced.plugins.IndirectSelection");
dojo.require("dijit.Menu");
dojo.require("dijit.MenuItem");

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
        alert("recording");
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
                alert(error);
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
                alert(error);
            }
        }
        // Call the asynchronous xhrGet
        dojo.xhrGet(cameraArgs);
        dojo.xhrGet(eventArgs);
        dojo.xhrGet(recordingArgs);
    }


});
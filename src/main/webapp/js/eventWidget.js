
dojo.provide("core.eventWidget");

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
dojo.declare('core.eventWidget', [dijit._Widget, dijit._Templated],{
    widgetsInTemplate : true,
    templatePath: "js/htmlTemplates/eventWidget.html",
    _this : null,

    postCreate : function(){
        _this = this;
        this.getEventsByHours(24);
    },

    getEventsByHours : function (hours){
        var xhrArgs = {
            url: "/api/event/hours/" + hours,
            handleAs: "text",
            load: function(data){
                _this.populateEventPane(data);
            },
            error: function(error){
                alert(error);
            }
        }
        // Call the asynchronous xhrGet
        var deferred = dojo.xhrGet(xhrArgs);
    },

    populateEventPane : function(events){

    }


});
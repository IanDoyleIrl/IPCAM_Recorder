
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
    currentEvent : null,
    eventStats : null,
    _this : null,

    postCreate : function(){
        _this = this;
        this.populateVariables();
    },

    convertDate : function(date){
        var utcSeconds = 1234567890;
        var d = new Date(0); // The 0 there is the key, which sets the date to the epoch
        d.setUTCSeconds(date);
        return d.format("dddd, MMMM Do YYYY, h:mm:ss a");;
    },

    loadLatestEventTable : function(){
        this.latestId.innerHTML = this.currentEvent.id;
        this.latestName.innerHTML = this.currentEvent.name;
        this.latestStarted.innerHTML = this.convertDate(this.currentEvent.timeStarted);
        this.latestEnded.innerHTML = this.convertDate(this.currentEvent.timeEnded);
        this.latestStatus.innerHTML = this.currentEvent.active;
        this.latestComments.innerHTML = this.currentEvent.comments;
    },

    populateVariables : function (){
        var eventArgs = {
            url: "/api/event/latest",
            handleAs: "json",
            load: dojo._base.lang.hitch(this, function(data){
                this.currentEvent = data;
                this.loadLatestEventTable();
            }),
            error: function(error){
                alert(error);
            }
        }
        dojo.xhrGet(eventArgs);
    },

    getEventByID : function (id){
        var statsArgs = {
            url: "/api/event/" + id,
            handleAs: "json",
            sync: false,
            load: function(data){
                _this.currentEvent = data;
            },
            error: function(error){
                alert(error);
            }
        }
        dojo.xhrGet(statsArgs);
    }




});
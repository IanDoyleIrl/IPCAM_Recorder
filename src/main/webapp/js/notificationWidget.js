
dojo.provide("core.notificationWidget");

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
dojo.require("dojox.widget.Roller");
dojo.require("dojox.socket");

//Create our widget!
dojo.declare('core.notificationWidget', [dijit._Widget, dijit._Templated],{
    widgetsInTemplate : true,
    templatePath: "js/htmlTemplates/notificationWidget.html",
    currentEventId : null,
    eventStats : null,
    recordingStats : null,
    cameras : null,
    _thisNotification : null,
    socket : null,
    roller : null,

    postCreate : function(){
        this.getEventStats();
        //this.createAndStartTicker();
        this.socket = dojox.socket({
            url: "/RealtimeUpdate"
        })
        dojo._base.lang.hitch(this, this.socket.on("message", dojo._base.lang.hitch(this, function(event){
            var data = dojo.fromJson(event.data);
            this.recordingStats = data.recordingStats;
            this.eventStats = data.eventStats;
            dojo._base.lang.hitch(this, this.updateItemsOnRoller());
        })));
        _thisNotification = this;
    },

    getEventStats : function(){
        var stats1 = {
            url: "/api/recording/stats",
            handleAs: "json",
            sync: true,
            load: function(data){
                _this.recordingStats = data;
            },
            error: function(error){
                alert(error);
            }
        }
        var stats2 = {
            url: "/api/event/stats",
            handleAs: "json",
            sync: true,
            load: function(data){
                _this.eventStats = data;
            },
            error: function(error){
                alert(error);
            }
        }
        // Call the asynchronous xhrGet
        dojo.xhrGet(stats1);
        dojo.xhrGet(stats2);
    },

    createAndStartTicker : function (){
        _thisNotification.roller = new dojox.widget.RollerSlide({
            items: _thisNotification.getRollerItems(),
            delay : 1000,
            title : "Status Updates"
        }, dojo.byId("rollerDiv"));

    },

    updateItemsOnRoller : function (items){
        if (_thisNotification.roller == null){
            _thisNotification.createAndStartTicker();
            return;
        }
        _thisNotification.roller.items = _thisNotification.getRollerItems();
        _thisNotification.roller.start();
    },

    getRollerItems : function(){
        var items = [];
        items.push(this.formatItemTable("Recording Table Size", this.recordingStats.tableSize));
        items.push(this.formatItemTable("Recording Image Count", this.recordingStats.totalImageCount));
        items.push(this.formatItemTable("Event Image Count", this.eventStats.totalEventImageCount));
        items.push(this.formatItemTable("Total Events", this.eventStats.totalEventCount));
        items.push(this.formatItemTable("Currently Active Event", this.eventStats.isEventActive));
        items.push(this.formatItemTable("Avg Images per Event", this.eventStats.averageImagesPerEvent));
        return items;
    },

    formatItemTable : function(label, value){
        var result = "<table width='300'>" +
            "<tr>" +
            "<td><b>" + label + ":<b></td>" +
            "<td>" + value + "</td>" +
            "</tr>" +
            "</table>"
        return result;
    },

    handlePlayPause : function(action){
        alert(action);
    }


});
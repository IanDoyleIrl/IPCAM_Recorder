
dojo.provide("core.eventWidget");

//Create our widget!
dojo.declare('core.eventWidget', [dijit._Widget, dijit._Templated],{

    widgetsInTemplate : true,
    templatePath: "js/htmlTemplates/eventWidget.html",
    currentEvent : null,
    eventStats : null,
    eventThis : null,
    eventTypeDropdown : null,
    eventPeriodDropdown : null,
    eventTableWidget : null,


    postCreate : function(){
        eventThis = this;
        this.populateVariables();
        this.loadEventDropdowns();
        this.eventTableWidget = new core.eventDetailsTableWidget({event : null}, this.eventTable);
    },

    getHoursFromDropdowns : function(){
        var value = eventThis.eventPeriodDropdown.value;
        var type = eventThis.eventTypeDropdown.value;
        if (type == null || value == null){
            return null;
        }
        if (type == "Hours"){
            return value;
        }
        if (type == "Days"){
            return value * 24;
        }
        if (type == "Weeks"){
            return (value * 24) * 7;
        }
        if (type == "Months"){
            return (((value * 24) * 7) * 30);
        }
    },

    updateEventHistoryPane : function(events){
        var table = dojo.byId("eventHistoryTable");
        if (events.events.length == 0){
            dojo.style(eventThis.eventHistoryTable, "display", "none");
            dojo.style(eventThis.noEventLabel, "display", "inline");
            return;
        }
        dojo.style(eventThis.noEventLabel, "display", "none");
        dojo.style(eventThis.eventHistoryTable, "display", "inline");
        for(var i=0; i<table.rows.length; i++){
            table.deleteRow(i);
        }
        var table = dojo.byId("eventHistoryTable");
        var row = table.insertRow(table.rows.length);
        var name = row.insertCell(0);
        var started = row.insertCell(1);
        name.style.whiteSpace="nowrap";
        started.style.whiteSpace="nowrap";
        dojo.style(name, "width", "65%");
        dojo.style(started, "right", "68%");
        dojo.style(started, "width", "30%");
        dojo.style(name, "nowrap", "true");
        dojo.style(started, "nowrap", "true")
        for(var i=0; i<events.events.length; i++){
            this.addRow(events.events[i]);
        }
    },

    clearTableHighlights : function(table, nonHighlightedStyle){
        for(var i=0; i<table.rows.length; i++){
            dojo.style(table.rows[i], nonHighlightedStyle);
        }
    },

    handleEventHistorySelection : function(evt){
        console.log("click");
        var event = evt.currentTarget.event;
        var row = evt.currentTarget;
        var selectedStyle =
        {
            fontWeight:         "bold",
            backgroundColor:    "rgb(190, 199, 236)"
        };
        var highlightedStyle =
        {
            fontWeight:    "bold"
        };
        var nonHighlightedStyle =
        {
            fontWeight:   "normal"
        };
        var nonSelectedStyle =
        {
            backgroundColor:    "white",
            fontWeight:         "normal"
        };
        eventThis.clearTableHighlights(dojo.byId("eventHistoryTable"), nonHighlightedStyle);
        if (evt.type == "mouseout"){
            eventThis.updateEventDetailsPane(eventThis.currentEvent);
        }
        if (evt.type == "mouseover"){
            dojo.style(row, highlightedStyle);
            eventThis.updateEventDetailsPane(event);
        }
        if (evt.type == "click"){
            //_tabManager.addNewTab(event);
            eventThis.currentEvent = event;
            eventThis.clearTableHighlights(dojo.byId("eventHistoryTable"), nonSelectedStyle);
            dojo.style(row, selectedStyle);
        }
        if (evt.type == "dblclick"){
            _tabManager.addNewTab(event);
            eventThis.currentEvent = event;
            eventThis.clearTableHighlights(dojo.byId("eventHistoryTable"), nonSelectedStyle);
            dojo.style(row, selectedStyle);
        }
    },

    addRow : function(event) {
        var table = dojo.byId("eventHistoryTable");
        var row = table.insertRow(table.rows.length);
        row.event = event;
        dojo.on(row, "mouseover, click, mouseout, dblclick", dojo._base.lang.hitch(this, function(evt){
            eventThis.handleEventHistorySelection(evt);
        }));
        var name = row.insertCell(0);
        var started = row.insertCell(1);
        name.style.whiteSpace="nowrap";
        started.style.whiteSpace="nowrap";
        dojo.style(name, "width", "65%");
        dojo.style(started, "right", "68%");
        dojo.style(started, "width", "30%");
        dojo.style(name, "nowrap", "true");
        dojo.style(started, "nowrap", "true")
        name.innerHTML = event.name;
        started.innerHTML = eventThis.convertDate(event.timeStarted, true);
        //ended.innerHTML = eventThis.convertDate(event.timeStarted, true);
    },


    handleHistorySelectionChange : function(){
        var hours = eventThis.getHoursFromDropdowns();
        var eventArgs = {
            url: "/api/event/hours/" + hours,
            handleAs: "json",
            load: function(info){
                eventThis.updateEventHistoryPane(info);
            },
            error: function(error){
               // alert(error);
            }
        }
        if (hours != null){
            dojo.xhrGet(eventArgs);
        }
    },


    loadEventDropdowns : function(){
        var eventArgs = {
            url: "/api/Settings/dropdownOptions/event/hours",
            handleAs: "json",
            load: function(info){
                var stateStore = new dojo.store.Memory({
                    data: info.values
                });
                eventThis.eventPeriodDropdown = new dijit.form.ComboBox({
                    style: "width: 100%",
                    id: "historyTimePeriodSelect",
                    name: "historyTimePeriodSelect",
                    value: "Select....",
                    store: stateStore,
                    searchAttr: "name",
                    onChange: dojo._base.lang.hitch(eventThis, "handleHistorySelectionChange")
                }, eventThis.eventTimePeriodDropdown);

            },
            error: function(error){
               // alert(error);
            }
        }
        dojo.xhrGet(eventArgs);
        var typeArgs = {
            url: "/api/Settings/dropdownOptions/event/type",
            handleAs: "json",
            load: function(info){
                var typeStore = new dojo.store.Memory({
                    data: info.values
                });
                eventThis.eventTypeDropdown = new dijit.form.ComboBox({
                    style: "width: 100%",
                    id: "eventTimeTypeSelect",
                    name: "eventTimeTypeSelect",
                    store: typeStore,
                    searchAttr: "name",
                    onChange: dojo._base.lang.hitch(eventThis, "handleHistorySelectionChange")
                }, eventThis.eventTimeTypeDropdown);

            },
            error: function(error){
                //alert(error);
            }
        }
        dojo.xhrGet(typeArgs);
    },

    convertDate : function(date, short){
        var time = moment(date);
        if (short == false){
            return time.format('DD/MM/YYYY HH:mm:ss');
        }
        else{
            return time.format('DD/MM-HH:mm');
        }
    },

    updateEventDetailsPane : function(evt){
        this.eventTableWidget.event = evt;
        this.eventTableWidget.update();
    },

    populateVariables : function (){
        var eventArgs = {
            url: "/api/event/latest",
            handleAs: "json",
            load: dojo._base.lang.hitch(this, function(data){
                this.currentEvent = data;
                this.updateEventDetailsPane(data);
            }),
            error: function(error){
               // alert(error);
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
               // alert(error);
            }
        }
        dojo.xhrGet(statsArgs);
    }




});
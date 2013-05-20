
dojo.provide("core.tabManager");

//Create our widget!
dojo.declare('core.tabManager', [dijit._Widget, dijit.layout.TabContainer, core.globalFunctionality],{

    tabs : [],
    singleEventTab : true,
    config : null,
    cameras : null,
    _tabManager : null,

    constructor: function(configObj){
        this.config = configObj;
    },

    postCreate : function(){
        _tabManager = this;
        if (this.config.overrideDefaultCamera == true){
            this.addNewLiveTab(this.getCameraFromId(this.config.overrideDefaultCamera));
        }
        else{
            this.cameras = this.getAllCameras();
            for(var i=0; i<this.cameras.length; i++) {
                if (this.cameras[i].active == true){
                    if (this.presentationMode == "windowed"){
                        //this._createOrUpdateTab(this.cameras[i]);
                    }
                }
            }
        }

    },

    getTabByEntity : function(entity){
        for(var i=0; i<this.tabs.length; i++) {
                var entry = this.tabs[i];
                if (entry.entity.id == entity.id && entry.entity.type == entity.type){
                    return entry;
                }
        }
    },

    getTabIdFromTypeAndEntity : function(type, entity){

    },

    addNewTab : function(entity){
        this._createOrUpdateTab(entity);
    },

    _createOrUpdateTab : function(entity){
        var tabId = this.getTabByEntity(entity);
        if (!tabId){
            var newPane = new core.streamingWidget();
            newPane.setEntity(entity);
            newPane.update();
            this.addChild(newPane);
            newPane.entity = entity;
            this.tabs.push(newPane);
            if (entity.type == "event" && this.singleEventTab){
                this.deleteAllBut(newPane, true);
            }
            this.selectTab(newPane);
        }
        else{
            this.selectTab(tabId);
        }
    },

    selectTab : function(entity){
        this.selectChild(entity);
    },

    removeTab : function(tab){
        if (this.tabs.length != 1){
            this.removeChild(tab);
        }
    },

    deleteAllBut : function(tab, eventTabsOnly){
        for(var i=0; i<this.tabs.length; i++) {
            if (eventTabsOnly){
                var entry = this.tabs[i];
                if (entry.entity.type == "event" && entry.entity.id != tab.entity.id){
                    this.removeChild(this.tabs[i]);
                }
            }
        }
        for(var i=0; i<this.tabs.length; i++) {
            if (eventTabsOnly){
                var entry = this.tabs[i];
                if (entry.entity.type == "event" && entry.entity.id != tab.entity.id){
                    this.tabs.splice(i, 1);
                }
            }
        }
    }


});
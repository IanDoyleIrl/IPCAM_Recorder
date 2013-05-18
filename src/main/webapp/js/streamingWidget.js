
dojo.provide("core.streamingWidget");

//Create our widget!
dojo.declare('core.streamingWidget', [core.globalFunctionality, dijit.layout.ContentPane, dijit._Widget, dijit._Templated],{

    widgetsInTemplate : true,
    templatePath: "js/htmlTemplates/streamingWidget.html",
    entity : null,
    handler : null,
    title : null,

    update : function(){
        var imgNode = this.streamDiv;
        if (this.entity.type == "camera"){
            if (imgNode != null){
                imgNode.src = "/Videostream?mode=live&cameraId=" + this.entity.id;
            }
            this.title = "Live - " + this.entity.name;
        }
        else if (this.entity.type == "event"){
            imgNode.src = "/Videostream?mode=event&eventId=" + this.entity.id;
            this.title = "Event " + this.entity.id;
            var table = new core.eventDetailsTableWidget({event: this.entity}, this.eventTable);
            this._createEventStreamHandler();
        }
    },

    postCreate : function(){

    },

    _createEventStreamHandler : function(){
        this.handler = new core.streamingHandlerWidget({event : this.entity, callback : this.handleEventStreamUpdate, streamingWidget : this});
    },

    handleEventStreamUpdate : function(data, refWidget){
        if (data.readyToStream){
            dojo.style(refWidget.loadingImg, "display", "none");
            dojo.style(refWidget.streamDiv, "display", "inline");
            refWidget.set("title", data.currentFrame + "/" + data.totalFrames);
        }
        if (!data.readyToStream){
            dojo.style(refWidget.loadingImg, "display", "inline");
            dojo.style(refWidget.streamDiv, "display", "none");
        }
        //this.set("title", data.currentFrame + "/" + data.totalFrames);
    },

    setEntity : function(entity){
        this.entity = entity;
    }


});

dojo.provide("core.streamingWidget");

//Create our widget!
dojo.declare('core.streamingWidget', [core.globalFunctionality, dijit.layout.ContentPane, dijit._Widget, dijit._Templated],{

    widgetsInTemplate : true,
    templatePath: "js/htmlTemplates/streamingWidget.html",
    entity : null,
    handler : null,
    title : null,
    streamId : null,
    socket : null,
    progressBar : null,

    update : function(){
        var imgNode = this.streamDiv;
        if (this.entity.type == "camera"){
            if (imgNode != null){
                imgNode.src = "/Videostream?mode=live&cameraId=" + this.entity.id;
            }
            this.title = "Live - " + this.entity.name;
        }
        else if (this.entity.type == "event"){
            imgNode.src;
            var data = {
                eventId : this.entity.id
            }
            var xhrArgs = {
                url: "/api/stream/eventStream/",
                postData: dojo.toJson(data),
                handleAs: "json",
                headers: { "Content-Type": "application/json"},
                load: dojo._base.lang.hitch(this, function(data){
                    this.streamId = data.streamId;
                    this._handleEventStreamResponse(data);
                }),
                error: function(error){
                    //alert(error)
                }
            }
            var deferred = dojo.rawXhrPost(xhrArgs);
            this.title = "Event " + this.entity.id;
            var table = new core.eventDetailsTableWidget({event: this.entity}, this.eventTable);
            this.progressBar = new core.streamProgressBar({
                stream : this
            }, this.progressDiv);
        }
        this._createVideoControls();
    },

    _handleProgressBarSelection : function(evt){
        console.log(evt);
    },

    _createStreamHandler : function(){
        this.socket = dojox.socket({
            url: "/StreamingHandler?streamId=" + this.streamId
        })
        this.socket.on("message", dojo._base.lang.hitch(this, function(event){
            this._handleStreamUpdate(event);
        }));
        this.socket.on("open", dojo._base.lang.hitch(this, function(event){
            var data = {
                action : "socketOpen"
            }
            this.socket.send(dojo.toJson(data));
        }));
    },

    _handleStreamUpdate : function (feed){
        var data = dojo.fromJson(feed.data);
        if (data.status == "READY"){
            var data = {
                action : "startRunning"
            }
            this.socket.send(dojo.toJson(data));
        }
        if (data.status == "RUNNING"){
            this.progressBar.set("value", feed.percentComplete);
            dojo.style(this.loadingImg, "display", "none");
            dojo.style(this.streamDiv, "display", "inline");
            dojo.style(this.videoControls, "display", "inline");
            dojo.style(this.progressDiv, "display", "inline");
            this.set("title", "Event: " + this.entity.id + " - Playing " + data.currentFrame + "/" + data.totalFrames);
        }
        if (data.status == "LOADING"){
            dojo.style(this.loadingImg, "display", "inline");
            dojo.style(this.streamDiv, "display", "none");
            dojo.style(this.videoControls, "display", "none");
            dojo.style(this.progressDiv, "display", "none");
            this.set("title", "Event: " + this.entity.id + " - Loading");
        }
        if (data.status == "FINISHED"){
            dojo.style(this.loadingImg, "display", "inline");
            dojo.style(this.streamDiv, "display", "none");
            dojo.style(this.videoControls, "display", "none");
            dojo.style(this.progressDiv, "display", "inline");
            this.set("title", "Event: " + this.entity.id + " - Stopped");
        }
        this.progressBar.set("value", data.percentComplete);
    },

    _handleEventStreamResponse : function(data){
        this.streamDiv.src = "/Videostream?mode=event&streamId=" + data.streamId;
        this._createStreamHandler();
    },

    jumpTo : function(data){
        this.socket.send(dojo.toJson(data));
    },

    _createVideoControls : function(evt){
        var playButton = new dijit.form.Button({
            label: "Pause",
            onClick: dojo._base.lang.hitch(this, "_handlePausePlayClick")
        }, this.PausePlayButton);
        var playButton = new dijit.form.Button({
            label: "FF",
            onClick: dojo._base.lang.hitch(this, "_handleFFClick")
        }, this.FFButton);
        var playButton = new dijit.form.Button({
            label: "RW",
            onClick: dojo._base.lang.hitch(this, "_handleRWClick")
        }, this.RWButton);
        var playButton = new dijit.form.Button({
            label: "Download",
            onClick: dojo._base.lang.hitch(this, "_handleDownloadClick")
        }, this.DownloadButton);
    },

    _handlePausePlayClick : function(evt){
        var data = {
            action : "pauseStream"
        }
        this.socket.send(dojo.toJson(data));
    },

    _handleFFClick : function(evt){
        var data = {
            action : "FFSteam"
        }
        this.socket.send(dojo.toJson(data));
    },

    _handleRWClick : function(evt){
        var data = {
            action : "RWStream"
        }
        this.socket.send(dojo.toJson(data));
    },

    _handleDownloadClick : function(evt){

    },

    _registerVideoControls : function(evt){
        dojo.connect(this.PausePlayButton, "onclick", function(evt){
            alert("123")
        });
        //dojo.connect(this.PausePlayButton, "onclick", dojo.hitch(this, this._handlePausePlayClick()));
       // dojo.connect(this.FFButton, "onclick", "_handleFFClick");
       // dojo.connect(this.RWButton, "onclick", "_handleRWClick");
       // dojo.connect(this.DownloadButton, "onclick", "_handleDownloadClick");
    },

    postCreate : function(){

    },

    _handleEventStreamUpdate : function(data, refWidget){
        if (data.readyToStream){
            dojo.style(refWidget.loadingImg, "display", "none");
            dojo.style(refWidget.streamDiv, "display", "inline");
            dojo.style(refWidget.videoControls, "display", "inline");
            this.set("title", data.currentFrame + "/" + data.totalFrames);
        }
        if (!data.readyToStream){
            dojo.style(refWidget.videoControls, "display", "none");
            dojo.style(refWidget.loadingImg, "display", "inline");
            dojo.style(refWidget.streamDiv, "display", "none");
        }
        //this.set("title", data.currentFrame + "/" + data.totalFrames);
    },

    setEntity : function(entity){
        this.entity = entity;
    }


});
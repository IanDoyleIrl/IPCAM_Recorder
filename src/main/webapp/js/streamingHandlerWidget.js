
dojo.provide("core.streamingHandlerWidget");

//Create our widget!
dojo.declare('core.streamingHandlerWidget', [dijit._Widget],{

    event : null,
    callback : null,
    socket : null,
    streamingWidget : null,

    postCreate : function(){
        this.socket = dojox.socket({
            url: "/StreamingHandler"
        })
        dojo._base.lang.hitch(this, this.socket.on("message", dojo._base.lang.hitch(this, function(event){
            dojo._base.lang.hitch(this.handleStreamUpdate(event));
        })));
        dojo._base.lang.hitch(this, this.socket.on("open", dojo._base.lang.hitch(this, function(event){
            this.socket.send(this.event.id);
        })));

    },

    handleStreamUpdate : function (feed){
        var data = dojo.fromJson(feed.data);
        this.callback(data, this.streamingWidget);
    }
});
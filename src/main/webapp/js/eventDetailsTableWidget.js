
dojo.provide("core.eventDetailsTableWidget");

//Create our widget!
dojo.declare('core.eventDetailsTableWidget', [dijit._Widget, dijit._Templated],{

    widgetsInTemplate : true,
    templatePath: "js/htmlTemplates/eventDetailsTableWidget.html",
    event : null,

    postCreate : function(){
        if(this.event != null){
            this.update();
        }
    },

    update : function(){
        this.id.innerHTML = this.event.id;
        this.name.innerHTML = this.event.name;
        this.started.innerHTML = this.convertDate(this.event.timeStarted, false);
        this.ended.innerHTML = this.convertDate(this.event.timeEnded, false);
        this.status.innerHTML = this.event.active;
        this.comments.innerHTML = this.event.comments;
    },

    convertDate : function(date, short){
        var time = moment(date);
        if (short == false){
            return time.format('DD/MM/YYYY HH:mm:ss');
        }
        else{
            return time.format('DD/MM-HH:mm');
        }
    }

});
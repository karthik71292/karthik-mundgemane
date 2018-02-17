50
};

var Monster = function(){
    this.pos=var canvas = document.getElementById("board");
    var ctx = canvas.getContext("2d");

    var cells = [];
    var cursor = {i:0,j:0};
    var selected = {i:-1,j:0};
    var objSelected = null;
    var properties = {
        offset: {x:150, y:100},
        size:  {i:-1,j:-1};
    this.power= 0;
    this.hp= 0;
    this.moveMatrix= [[0,1,0],
        [1,0,1],
        [0,1,0]];
    this.powers = [];
    this.selected = false;
    this.reducePower = function(r){
        this.power-=r;
    };
    this.color = '#FFF';
    this.moveTo = function(i, j){
        this.drawAvalaibleMoves(false);
        if(this.pos.i!=-1){
            cells[this.pos.i][this.pos.j].obj=null;
            paintCell(this.pos.i, this.pos.j);
        }
        this.pos.i = i;
        this.pos.j = j;
        cells[i][j].obj=this;
    };
    this.setSelected = function(selected){
        this.selected = selected;
        if(!selected){
            this.drawAvalaibleMoves(false);
        }
    };
    this.actionTo = function(i, j){
        //validate move: assuming square matrix
        var limit = Math.floor(this.moveMatrix.length/2);
        var moveI = limit-(this.pos.i-i);
        var moveJ = limit-(this.pos.j-j);
        if(moveI<0||moveI>=this.moveMatrix.length||moveJ<0||moveJ>=this.moveMatrix.length) return false;
        if(this.moveMatrix[moveI][moveJ]==0) return false;
        this.moveTo(i, j);
        return true;
    };
    this.drawAvalaibleMoves = function(show){
        var radius = Math.floor(this.moveMatrix.length/2);
        var moveI = 0;
        var moveJ = 0;
        ctx.fillStyle = 'rgba(235,27,59,0.5)';
        for(var i = 0; i<this.moveMatrix.length; i++){
            for(var j = 0; j<this.moveMatrix[i].length; j++){
                if(this.moveMatrix[i][j]==0) continue;
                moveI = this.pos.i-radius+i;
                moveJ = this.pos.j-radius+j;
                if(moveI<0||moveI>=8||moveJ<0||moveJ>=8) continue;
                if(show){
                    ctx.fillRect(properties.offset.x+moveI*properties.size, properties.offset.y+moveJ*properties.size, properties.size, properties.size);
                }else{
                    paintCell(moveI, moveJ);
                }
            }
        }
    };
    this.drawMe = function(){
        ctx.fillStyle=this.color;
        ctx.fillRect(properties.offset.x+properties.size*0.15+this.pos.i*properties.size, properties.offset.y+properties.size*0.15+this.pos.j*properties.size, properties.size*0.7, properties.size*0.7);
        if(this.selected){
            this.drawAvalaibleMoves(true);
        }
    };
};

    var paintCell = function(i, j){
        if(i==-1)return;
        if((i+j)%2==0){
            ctx.fillStyle = '#594E3F';
        }else{
            ctx.fillStyle = '#E7DAAA';
        }
        ctx.fillRect(150+i*50, 100+j*50, 50, 50);
        var obj = cells[i][j].obj;
        if(obj){
            obj.drawMe();
        }
    };

    var paintSelectedCell = function(){
        if(selected.i==-1)return;
        ctx.fillStyle = 'rgb(201,250,244)';
        ctx.fillRect(150+selected.i*50, 100+selected.j*50, 50, 50);
        ctx.strokeStyle = '#FFF'
        ctx.strokeRect(151+selected.i*50, 101+selected.j*50, 48, 48);
        var obj = cells[selected.i][selected.j].obj;
        if(obj){
            //obj.drawMe();
        }
    };

    var initBoard = function(){
        for(var i=0; i<8; i++){
            cells.push([]);
            for(var j=0; j<8; j++){
                cells[i].push({obj:null});
            }
        }
    };
    var drawBoard = function(){
        ctx.strokeRect(151,101,400,400);
        for(var i=0; i<8; i++){
            for(var j=0; j<8; j++){
                paintCell(i, j);
            }
        }
    };

    var getFromCell= function(i, j){
        return cells[i][j].obj;
    };

    var trackCursor = function(xpos, ypos){
        if(xpos>=150&&xpos<550&&
            ypos>=100&&ypos<500){
            var i = parseInt((xpos-150)/50);
            var j = parseInt((ypos-100)/50);
            if(cursor.i!=i||cursor.j!=j){
                if(i!=selected.i || j !=selected.j){
                    ctx.fillStyle = 'rgba(235,27,59,0.5)';
                }else{
                    ctx.fillStyle = 'rgba(201,250,244,0.5)';
                }
                ctx.fillRect(150+i*50, 100+j*50, 50, 50);
                if(cursor.i!=selected.i || cursor.j!=selected.j){
                    paintCell(cursor.i, cursor.j);
                }else {
                    paintSelectedCell();
                }
                cursor.i = i;
                cursor.j = j;
            }
        }else {
            paintCell(cursor.i, cursor.j);
            cursor.i = -1;
        }

        for(var i=0; i<monsters.length; i++){
            monsters[i].drawMe();
        }

    };

    var selectObject = function(obj){
        if(obj==objSelected){
            if(obj){
                objSelected.setSelected(false);
            }
            objSelected = null;
            return;
        }
        if(objSelected){
            objSelected.setSelected(false);
        }
        objSelected = obj;
        if(objSelected){
            objSelected.setSelected(true);
        }
    };

    var selectCell = function(){
        if(cursor.i==-1){
            paintCell(selected.i, selected.j);
            selected.i=0;
            selected.j=0;
            selectObject(null);
            return;
        }
        var something = getFromCell(cursor.i, cursor.j);
        if(cursor.i!=selected.i||cursor.j!=selected.j){
            if(objSelected && objSelected.actionTo(cursor.i, cursor.j)){
                return;
            }

            paintCell(selected.i, selected.j);

        }else{
            paintCell(selected.i, selected.j);
            selected.i = -1;
            selectObject(something);
            return;
        }
        selectObject(something)
        selected.i = cursor.i;
        selected.j = cursor.j;
        paintSelectedCell();
    };

    var monsters = [];
    var createMonsters = function(){
        var m1 = new Monster();
        m1.power = 5;
        m1.moveTo(1, 1);
        m1.color='#000';
        m1.moveMatrix=[[1,0,1,0,1],
            [0,1,1,1,1],
            [1,1,0,1,1],
            [0,1,1,1,0],
            [1,0,1,0,1]];
        var m2 = new Monster();
        m2.power = 10;
        m2.moveTo(5, 6);
        var m3 = new Monster();
        m3.power = 10;
        m3.moveTo(1, 5);
        m3.moveMatrix=[[1,0,1],
            [0,0,0],
            [1,0,1]]

        var m4 = new Monster();
        m4.power = 5;
        m4.moveTo(6, 1);
        m4.color='#000';
        m4.moveMatrix=[[0,1,0,1,0],
            [1,0,0,0,1],
            [0,0,0,0,0],
            [1,0,0,0,1],
            [0,1,0,1,0]];

        m1.drawMe();
        m2.drawMe();
        m3.drawMe();
        m4.drawMe();
        monsters.push(m1);
        monsters.push(m2);
        monsters.push(m3);
        monsters.push(m4);
    };

    initBoard();
    drawBoard();
    createMonsters();
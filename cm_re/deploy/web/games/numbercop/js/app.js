var __extends = (this && this.__extends) || function (d, b) {
    for (var p in b) if (b.hasOwnProperty(p)) d[p] = b[p];
    function __() { this.constructor = d; }
    d.prototype = b === null ? Object.create(b) : (__.prototype = b.prototype, new __());
};
var GameSettings = (function () {
    function GameSettings() {
        this._score = 0;
        this._multiple = 2;
        this._difficulty = 1;
        this.optionCount = 0;
        this.gameSpace = { cx: 0, cy: 0 };
        this.menuConfig = { startX: 0, startY: 0, align: 'center' };
        this.scoreMesg = { "0": "Work Hard!", "1": "Need to improve!", "2": "Ok!", "3": "Good!", "4": "Great!", "5": "Excellant!" };
        if (GameSettings._instance) {
            throw new Error("Error: Instantiation failed: Use GameSettings.getInstance() instead of new.");
        }
        GameSettings._instance = this;
    }
    GameSettings.getInstance = function () {
        return GameSettings._instance;
    };
    GameSettings.prototype.setScore = function (value) {
        this._score = value;
    };
    GameSettings.prototype.getScore = function () {
        return this._score;
    };
    GameSettings.prototype.addPoints = function (value) {
        this._score += value;
    };
    GameSettings.prototype.removePoints = function (value) {
        this._score -= value;
    };
    GameSettings.prototype.setGameType = function (val) {
        this._multiple = val;
    };
    GameSettings.prototype.getGameType = function () {
        return this._multiple;
    };
    GameSettings.prototype.getGTPhrase = function () {
        var m = this._multiple;
        if (isNaN(m)) {
            if (m === 'prime') {
                return "Prime";
            }
            if (m === 'rational') {
                return "Rational";
            }
            if (m === 'sq') {
                return "Perfect Square";
            }
        }
        else {
            return "Multiple of " + m;
        }
    };
    GameSettings.prototype.setGameDifficulty = function (val) {
        this._difficulty = val;
    };
    GameSettings.prototype.getGameDifficulty = function () {
        return this._difficulty;
    };
    return GameSettings;
}());
GameSettings._instance = new GameSettings();
var SimpleGame = (function () {
    function SimpleGame() {
        this.game = new Phaser.Game(800, 600, Phaser.AUTO, 'game', { preload: this.preload, create: this.create });
    }
    SimpleGame.prototype.preload = function () {
        ////this.game.load.image('preloadbar', 'assets/images/preloader-bar.png');
    };
    SimpleGame.prototype.create = function () {
        this.game.state.add("Boot", Boot);
        this.game.state.add("Preloader", Preloader);
        this.game.state.add("Splash", Splash);
        this.game.state.add("Menu", Menu);
        this.game.state.add("GameScene", GameScene);
        this.game.state.add("GameOver", GameOver);
        this.game.state.start('Boot', true, false);
    };
    return SimpleGame;
}());
var Boot = (function (_super) {
    __extends(Boot, _super);
    function Boot() {
        return _super.apply(this, arguments) || this;
    }
    Boot.prototype.preload = function () {
    };
    Boot.prototype.handleIncorrect = function () {
        if (!this.game.device.desktop) {
            document.getElementById("turn").style.display = "block";
        }
    };
    Boot.prototype.handleCorrect = function () {
        if (!this.game.device.desktop) {
            document.getElementById("turn").style.display = "none";
        }
    };
    Boot.prototype.create = function () {
        this.settings = GameSettings.getInstance();
        this.settings.game = this.game;
        this.settings.optionCount = 0;
        this.settings.menuConfig.align = 'center';
        this.settings.menuConfig.startX = this.game.world.centerX;
        this.settings.menuConfig.startY = this.game.world.height - 40;
        this.settings.gameSpace.cx = this.game.world.centerX;
        this.settings.gameSpace.cy = this.game.world.centerY;
        this.input.maxPointers = 1;
        this.stage.disableVisibilityChange = true;
        this.game.stage.backgroundColor = '#000';
        //scaling options
        this.game.scale.scaleMode = Phaser.ScaleManager.SHOW_ALL;
        //this.game.scale.forceOrientation(true, false);
        //this.game.scale.enterIncorrectOrientation.add(this.handleIncorrect);
        //this.game.scale.leaveIncorrectOrientation.add(this.handleCorrect);
        //have the game centered horizontally
        this.game.scale.pageAlignHorizontally = true;
        this.game.scale.pageAlignVertically = true;
        if (this.game.device.desktop) {
        }
        else {
        }
        //physics system
        this.game.physics.startSystem(Phaser.Physics.ARCADE);
        this.game.state.start('Preloader', true, false);
    };
    return Boot;
}(Phaser.State));
var Splash = (function (_super) {
    __extends(Splash, _super);
    function Splash() {
        return _super.apply(this, arguments) || this;
    }
    Splash.prototype.preload = function () {
        var assets = 'assets/';
        this.load.image('ground', assets + 'ground_soil.png');
        this.load.image('car', assets + 'car.png');
        this.load.image('numbox', assets + 'numbox.png');
        this.load.image('sun', assets + 'sun.png');
        this.load.image('moon', assets + 'moon.png');
        this.load.image('city', assets + 'city.png');
        this.load.image('star', assets + 'star.png');
        //this.load.script("utils.js", "js/utils.js");
        this.add.sprite(0, 0, "splash");
        var loadingBar = this.add.sprite(this.world.centerX - (387 / 2), this.game.world.height - 70, "loading");
        var text = this.game.add.text(this.game.world.centerX, this.game.world.height - 20, "Loading...", {
            font: "25px Arial",
            fill: "#fff",
            align: "center"
        });
        text.anchor.set(0.5, 0.5);
        this.load.setPreloadSprite(loadingBar);
    };
    Splash.prototype.create = function () {
        this.game.state.start('Menu', true, false);
    };
    return Splash;
}(Phaser.State));
var Menu = (function (_super) {
    __extends(Menu, _super);
    function Menu() {
        return _super.apply(this, arguments) || this;
    }
    Menu.prototype.preload = function () { };
    Menu.prototype.init = function () {
        this.settings = GameSettings.getInstance();
        this.optionCount = 0;
        this.game.add.sprite(0, 0, 'splash');
        this.settings.addBtn = this.addMenuOption;
        this.menuConfig = { align: 'center', startX: this.settings.menuConfig.startX, startY: this.settings.menuConfig.startY };
    };
    Menu.prototype.addMenuOption = function (text, callback, className) {
        if (className === void 0) { className = 'default'; }
        var game = this.game;
        var style;
        // this is a wrapped function
        (function () {
            // the variables declared here will not be scoped anywhere and will only be accessible in this wrapped function
            var defaultColor = "white", highlightColor = "#FEFFD5";
            style = {
                navitem: {
                    base: {
                        font: '20pt Monospace',
                        align: 'left',
                        srokeThickness: 4
                    },
                    default: {
                        fill: defaultColor,
                        stroke: 'rgba(0,0,0,0)'
                    },
                    inverse: {
                        fill: 'black',
                        stroke: 'black'
                    },
                    hover: {
                        fill: highlightColor,
                        stroke: 'rgba(200,200,200,0.5)'
                    }
                }
            };
            for (var key in style.navitem) {
                if (key !== "base") {
                    Object.assign(style.navitem[key], style.navitem.base);
                }
            }
        })();
        // use the className argument, or fallback to menuConfig, but
        // if menuConfig isn't set, just use "default"
        className || (className = this.menuConfig.className || 'default');
        // set the x coordinate to game.world.center if we use "center"
        // otherwise set it to menuConfig.startX
        var x = this.menuConfig.startX === "center" ?
            game.world.centerX :
            this.menuConfig.startX;
        // set Y coordinate based on menuconfig
        var y = this.menuConfig.startY;
        // create
        var graphics = game.add.graphics(0, 0);
        var sprite = game.add.sprite(x, y);
        sprite.addChild(graphics);
        //sprite.anchor.set(0.5);
        graphics.lineStyle(1, 0xffffff, 0.4);
        graphics.beginFill(0xff3300, 0.4);
        graphics.drawRect(-100, -25, 200, 40);
        var txt = game.add.text(x, (this.optionCount * 80) + y, text, style.navitem[className]);
        // use the anchor method to center if startX set to center.
        txt.anchor.setTo(this.menuConfig.align === "center" ? 0.5 : 0.0);
        txt.inputEnabled = true;
        txt.input.useHandCursor = true;
        txt.events.onInputUp.add(callback);
        sprite.inputEnabled = true;
        sprite.input.useHandCursor = true;
        sprite.events.onInputUp.add(callback);
        txt.events.onInputOver.add(function (target) {
            //target.setStyle(style.navitem.hover);
            //sprite.tint=0x000000
            console.log("OVER");
            //game.add.tween(graphics.scale).to( { x: 1.25,y:1.25 }, 50, "Linear", true);
        });
        txt.events.onInputOut.add(function (target) {
            //target.setStyle(style.navitem[className]);
            //sprite.scale.setTo(1,1);
            //game.add.tween(graphics.scale).to( { x: 1,y:1 }, 200, "Linear", true);
        });
        this.optionCount++;
    };
    Menu.prototype.create = function () {
        var game = this.game;
        game.stage.disableVisibilityChange = true;
        this.addMenuOption('Start', function () {
            game.state.start("GameScene");
        });
        //
        var me = this;
        //Set the background colour of the game
        me.game.stage.backgroundColor = "34495f";
        //Declare assets that will be used as tiles
        me.tileLetters = [
            ['x', 2, 3], [4, 5, 6], [7, 8, 9], ["Prime"], ["Perfect Square"]
        ];
        //What colours will be used for our tiles?
        me.tileColors = [
            '#ffffff', '#CD853F'
        ];
        //Set the width and height for the tiles
        me.tileWidth = 50;
        me.tileHeight = 50;
        //This will hold all of the tile sprites
        me.tiles = me.game.add.group();
        //Initialise tile grid, this array will hold the positions of the tiles
        //Create whatever shape you'd like
        me.tileGrid = [
            ['x', 2, 3], [4, 5, 6], [7, 8, 9], ["Prime"], ["Perfect Square"]
        ];
        me.boardWidth = me.tileGrid[0].length * me.tileWidth;
        me.boardHeight = me.tileGrid[0].length * me.tileHeight;
        //We want to keep a buffer on the left and top so that the grid
        //can be centered
        me.leftBuffer = (me.game.width - me.boardWidth) / 2;
        me.topBuffer = (me.game.height - me.boardHeight) / 2;
        me.initTiles();
    };
    Menu.prototype.initTiles = function () {
        var _this = this;
        var me = this;
        var currentGame = this.settings.getGameType();
        var hicol = parseInt(me.tileColors[1].substring(1), 16);
        //Loop through each column in the grid
        for (var i = 0; i < me.tileGrid.length; i++) {
            //Loop through each position in a specific column, starting from the top
            for (var j = 0; j < me.tileGrid[i].length; j++) {
                //Add the tile to the game at this grid position
                var tile = me.addTile(j, i, i > 2 ? 4 : 1);
                if (tile.tileLetter != 'x') {
                    tile.inputEnabled = true;
                    tile.input.useHandCursor = true;
                    tile.events.onInputUp.add(function (t) {
                        console.log(t);
                        console.log("Tile Clicked:", t.tileLetter);
                        _this.settings.setGameType(t.tileLetter);
                        me.setTileDefTint();
                        t.tint = hicol;
                        t.isclicked = !false;
                    }, this);
                    tile.events.onInputOver.add(function (t) {
                        //console.log(t);
                        //console.log(tile);
                        t.tint = hicol;
                        //t.isclicked=false
                    });
                    tile.events.onInputOut.add(function (t) {
                        if (!t.isclicked) {
                            t.tint = 0xffffff;
                        }
                    });
                    if (currentGame === tile.tileLetter) {
                        tile.tint = hicol;
                        tile.isclicked = true;
                    }
                }
                //Keep a track of the tiles position in our tileGrid
                me.tileGrid[i][j] = tile;
            }
        }
    };
    Menu.prototype.setTileDefTint = function () {
        var __tileG = this;
        var len = __tileG.tileGrid.length;
        var l2;
        var tile;
        for (var i = 0; i < len; i++) {
            l2 = __tileG.tileGrid[i].length;
            for (var j = 0; j < l2; j++) {
                tile = __tileG.tileGrid[i][j];
                tile.isclicked = false;
                tile.tint = parseInt(__tileG.tileColors[0].substring(1), 16);
            }
        }
    };
    Menu.prototype.addTile = function (x, y, wc) {
        if (wc === void 0) { wc = 1; }
        var me = this;
        //Choose a random tile to add
        var tileLetter = me.tileLetters[y][x];
        var tileColor = me.tileColors[0];
        var tileToAdd = me.createTile(tileLetter, tileColor, wc);
        var tw = me.tileWidth * wc;
        var th = wc === 1 ? me.tileHeight : y === 3 ? me.tileHeight : me.tileHeight - 10;
        var off = y > 3 ? y - 3 : 0;
        var xoff = (y > 2 ? 1 : 0) * me.tileWidth / 2;
        //Add the tile at the correct x position, but add it to the top of the game (so we can slide it in)
        var tile = me.tiles.create(me.leftBuffer - xoff + (x * tw) + tw / 2, me.topBuffer - 20 + (y * me.tileHeight + (me.tileHeight / 2)) - (10 * off), tileToAdd);
        //Set the tiles anchor point to the center
        tile.anchor.setTo(0.5, 0.5);
        //Keep track of the type of tile that was added
        tile.tileLetter = this.getNumberType(tileLetter);
        return tile;
    };
    Menu.prototype.getNumberType = function (n) {
        if (isNaN(n)) {
            var _n = n.toLowerCase();
            if (_n === "prime") {
                n = _n;
            }
            else if (_n === "perfect square") {
                n = "sq";
            }
            else if (_n === "rational") {
                n = _n;
            }
        }
        else {
        }
        return n;
    };
    Menu.prototype.createTile = function (letter, color, wc) {
        if (wc === void 0) { wc = 1; }
        var me = this;
        var tw = me.tileWidth * wc;
        var th = wc === 1 ? me.tileHeight : me.tileHeight - 10;
        var tile = me.game.add.bitmapData(tw, th);
        tile.ctx.rect(5, 5, tw - 5, th - 5);
        tile.ctx.fillStyle = color;
        tile.ctx.fill();
        tile.ctx.font = wc === 1 ? '30px Monospace' : '17px Monospace';
        tile.ctx.textAlign = 'center';
        tile.ctx.textBaseline = 'middle';
        tile.ctx.fillStyle = '#fff';
        if (color == '#ffffff') {
            tile.ctx.fillStyle = '#000000';
        }
        tile.ctx.fillText(letter, tw / 2, th / 2);
        return tile;
    };
    return Menu;
}(Phaser.State));
var Preloader = (function (_super) {
    __extends(Preloader, _super);
    function Preloader() {
        return _super.apply(this, arguments) || this;
    }
    Preloader.prototype.preload = function () {
        var assets = 'assets/';
        this.load.image('loading', 'assets/loading.png');
        this.load.image('splash', assets + 'number_cop_splash.png');
    };
    Preloader.prototype.create = function () {
        this.game.state.start('Splash', true, false);
        // this.game.state.start('GameScene', true, false);
    };
    return Preloader;
}(Phaser.State));
var GameOver = (function (_super) {
    __extends(GameOver, _super);
    function GameOver() {
        return _super.apply(this, arguments) || this;
    }
    GameOver.prototype.preload = function () { };
    GameOver.prototype.init = function (score) {
        console.log(score);
        this.settings = GameSettings.getInstance();
        this.scoreObj = score;
        this.utils = Utils.getInstance();
        this.game.add.sprite(0, 0, 'splash');
        this.optionCount = 0;
        this.menuConfig = { startX: 'center', startY: this.game.world.height - 40 };
    };
    GameOver.prototype.create = function () {
        var _this = this;
        this.createScorePanel();
        this.optionCount = 0;
        this.settings.optionCount = 0;
        this.settings.addBtn("Play again", function () {
            _this.game.state.start("Menu", true, false);
        });
    };
    GameOver.prototype.createScorePanel = function () {
        var game = this.game;
        var w = 320;
        var h = this.game.world.height - 100;
        var x = this.settings.gameSpace.cx - (w / 2);
        var y = 20; //this.settings.gameSpace.cy;
        var graphics = game.add.graphics(0, 0);
        var sprite = game.add.sprite(x, y);
        sprite.addChild(graphics);
        //sprite.anchor.setTo(0.5,0.5);
        graphics.lineStyle(2, 0xffffff, 1);
        graphics.beginFill(0xCD853F, 0.9);
        graphics.drawRect(0, 0, w, h);
        var per = this.scoreObj.percent;
        var p = 0;
        if (per > 20 && per < 50) {
            p = 1;
        }
        if (per >= 50 && per < 60) {
            p = 2;
        }
        if (per >= 60 && per < 75) {
            p = 3;
        }
        if (per >= 75 && per < 100) {
            p = 4;
        }
        if (per === 100) {
            p = 5;
        }
        this.showStars(p, x + 35, y + 20);
        this.showPerformance(p, this.settings.gameSpace.cx, y + 20 + 50);
        this.showResult(x + 10, y + 20 + 50 + 60);
    };
    GameOver.prototype.showStars = function (p, x, y) {
        var group = this.game.add.group();
        group.width = 40;
        group.height = 40;
        for (var i = 0; i < 5; i++) {
            group.create(0, 0, 'star');
        }
        group.setAll('anchor.x', 0.5);
        group.setAll('anchor.y', 0.5);
        group.width = 40;
        group.height = 40;
        group.align(5, 1, 64, 40, Phaser.CENTER);
        group.x = x;
        group.y = y;
        var l = group.children.length;
        for (var k = 0; k < l; k++) {
            var item = group.children[k];
            if (k >= p) {
                item.alpha = 0.3;
            }
        }
    };
    GameOver.prototype.showPerformance = function (p, x, y) {
        var msg = this.settings.scoreMesg[p];
        var text = this.game.add.text(x, y, msg, { font: "30px Monospace", fill: "yellow", align: "center" });
        text.anchor.setTo(0.5, 0);
    };
    GameOver.prototype.showResult = function (x, y) {
        var txt = this.getScoreText();
        var style = { font: 'bold 16px MonoSpace', fill: 'white', align: 'left', wordWrap: true, wordWrapWidth: 300 };
        var text = this.game.add.text(x, y, txt, style);
        // text.anchor.set(0.5);
    };
    GameOver.prototype.getScoreText = function () {
        var s = this.scoreObj;
        var c = s.correct;
        var m = s.missed.miss;
        var i = s.incorrect.hit;
        var mul = this.settings.getGameType();
        //var mtxt=isNaN(mul)?this.getMText(mul):"multiple of "+mul;
        var cht = this.utils.getMText(mul, c.hit.length, false); //(c.hit.length > 1 ? " are " : " is ") + mtxt;
        cht += "\n";
        if (!c.hit.length) {
            cht = "";
        }
        var cmt = this.utils.getMText(mul, c.miss.length, true); //(c.miss.length > 1 ? " are not " : " is not ") + mtxt;
        if (!c.miss.length) {
            cmt = "-";
        }
        cmt += "\n";
        var mmt = this.utils.getMText(mul, m.length, false); //(m.length > 1 ? " are " : " is ") + mtxt;
        if (!m.length) {
            mmt = "";
        }
        mmt += "\n";
        var iht = this.utils.getMText(mul, i.length, true); //(i.length > 1 ? " are not " : " is not ") + mtxt;
        if (!i.length) {
            iht = "-";
        }
        iht += "\n";
        var ch = this.utils.uniqueArr(c.hit);
        var cms = this.utils.uniqueArr(c.miss);
        var cm = this.utils.uniqueArr(m);
        var ci = this.utils.uniqueArr(i);
        var cstr = "Correct:\n" + ch.join(", ") + cht;
        cstr += cms.join(", ") + cmt;
        cstr += "Missed:\n" + cm.join(", ") + mmt;
        cstr += "Incorrect:\n" + ci.join(", ") + iht;
        return cstr;
    };
    GameOver.prototype.restart = function () {
    };
    return GameOver;
}(Phaser.State));
var GameScene = (function (_super) {
    __extends(GameScene, _super);
    function GameScene() {
        var _this = _super.apply(this, arguments) || this;
        _this.wrapping = true;
        _this.stopped = false;
        _this.wraps = 0;
        //
        _this.multiple = 3;
        _this.numArray = [];
        _this.curIndex = 0;
        _this.gameScore = 0;
        _this.totalScore = 0;
        _this.percent = 0;
        _this.difficulty = 0; //"low";
        _this.numlimit = [5, 20, 20, 20];
        _this.correctNums = [];
        _this.incorrectNums = [];
        _this.isCorrect = false;
        _this.correct = 0;
        _this.incorrect = 0;
        _this.hitCorrect = [];
        _this.hitInCorrect = [];
        return _this;
    }
    GameScene.prototype.preload = function () {
        this.gw = this.game.world.width;
        this.utils = Utils.getInstance();
        this.settings = GameSettings.getInstance();
        this.multiple = this.settings.getGameType();
        this.difficulty = this.settings.getGameDifficulty();
        this.game.time.advancedTiming = true;
        this.gm = new GameManager(this.difficulty || 1, this.multiple);
        this.generateNumbers();
    };
    GameScene.prototype.create = function () {
        this.dayCycle = new DayCycle(this.game, 60000);
        var bgBitMap = this.game.add.bitmapData(this.game.width, this.game.height);
        bgBitMap.ctx.rect(0, 0, this.game.width, this.game.height);
        bgBitMap.ctx.fillStyle = '#b2ddc8';
        bgBitMap.ctx.fill();
        this.backgroundSprite = this.game.add.sprite(0, 0, bgBitMap);
        this.sunSprite = this.game.add.sprite(50, -250, 'sun');
        this.moonSprite = this.game.add.sprite(this.game.width - (this.game.width / 4), this.game.height * 2, 'moon');
        this.sunSprite.fixedToCamera = true;
        this.moonSprite.fixedToCamera = true;
        this.backgroundSprite.fixedToCamera = true;
        //
        this.game.world.setBounds(0, 0, this.game.width * 3, this.game.height);
        this.car = this.game.add.sprite(this.game.width / 2, this.game.height - 120, 'car');
        this.car.anchor.set(0.5, 0.5);
        this.ground = this.game.add.tileSprite(0, this.game.height - 70, this.game.world.width, 70, 'ground');
        this.cityscape = this.game.add.tileSprite(0, this.game.height - 441, 4096, 371, 'city');
        this.cityscape.fixedToCamera = true;
        this.countertext = this.game.add.text(10, 10, 'Number Count: 0', { font: "20px Monospace", fill: "#ffffff", align: "center" });
        //this.countertext.anchor.setTo(0.5, 0.5);
        this.countertext.fixedToCamera = true;
        this.messagetext = this.game.add.text(this.settings.gameSpace.cx, 100, 'TEST', { font: "30px Monospace", fill: "#ffffff", align: "center" });
        this.messagetext.anchor.setTo(0.5, 0.5);
        this.messagetext.scale.set(0, 0);
        this.messagetext.fixedToCamera = true;
        var gtxt = this.settings.getGTPhrase();
        this.typetext = this.game.add.text(this.gw - 10, 10, gtxt, { font: "20px Monospace", fill: "#ffffff", align: "right" });
        this.typetext.anchor.setTo(1, 0);
        this.typetext.fixedToCamera = true;
        // this.renderNumbers();
        var backgroundSprites = [
            { sprite: this.backgroundSprite, from: 0x1f2a27, to: 0xB2DDC8 },
            { sprite: this.ground, from: 0x202b28, to: 0x82AD9D },
            { sprite: this.cityscape, from: 0x202b28, to: 0x82AD9D } /*,
            { sprite: this.car, from: 0x202b28, to: 0x82AD9D }*/
        ];
        this.dayCycle.initShading(backgroundSprites);
        this.dayCycle.initSun(this.sunSprite);
        this.dayCycle.initMoon(this.moonSprite);
        this.game.world.bringToTop(this.cityscape);
        this.game.world.bringToTop(this.ground);
        this.game.world.bringToTop(this.car);
        this.game.physics.arcade.enable(this.car);
        this.game.physics.arcade.enable(this.ground);
        this.game.physics.arcade.enable(this.cityscape);
        //player gravity
        this.car.body.gravity.y = 1000;
        //so player can walk on ground
        this.ground.body.immovable = true;
        this.ground.body.allowGravity = false;
        this.cityscape.body.immovable = true;
        this.cityscape.body.allowGravity = true;
        this.game.camera.follow(this.car);
        //move player with cursor keys
        this.cursors = this.game.input.keyboard.createCursorKeys();
        //...or by swiping
        this.swipe = this.game.input.activePointer;
    };
    GameScene.prototype.update = function () {
        this.game.physics.arcade.collide(this.car, this.ground, this.empty, null, this);
        this.game.physics.arcade.collide(this.car, this.numbers, this.playerHit, null, this);
        //this.game.physics.arcade.overlap(this.car, this.mounds, this.collect, this.checkDig, this);
        if (!this.stopped) {
            this.car.body.velocity.x = 512;
            this.cityscape.tilePosition.x -= 5;
            //We do a little math to determine whether the game world has wrapped around.
            //If so, we want to destroy everything and regenerate, so the game will remain random
            // console.log(this.car.x,this.game.width);
            if (!this.wrapping && this.car.x < this.game.width) {
                //Not used yet, but may be useful to know how many times we've wrapped
                this.wraps++;
                console.log("WRAPPED:", this.car.x, this.cityscape.x);
                //We only want to destroy and regenerate once per wrap, so we test with wrapping var
                this.wrapping = true;
                if (this.numbers) {
                    this.numbers.destroy();
                }
                this.renderNumbers();
                // this.envObjects.destroy();
                this.generateenvobj();
                this.game.world.bringToTop(this.cityscape);
                this.game.world.bringToTop(this.ground);
                this.game.world.bringToTop(this.car);
                this.game.world.bringToTop(this.numbers);
            }
            else if (this.car.x >= this.game.width) {
                this.wrapping = false;
            }
            if (this.car.x >= this.game.width) {
            }
            if (this.swipe.isDown && (this.swipe.positionDown.y > this.swipe.position.y)) {
                this.playerJump();
            }
            else if (this.cursors.up.isDown) {
                this.playerJump();
            }
            this.game.world.wrap(this.car, -(this.game.width / 2) - 10, false, true, false);
        }
    };
    GameScene.prototype.render = function () {
    };
    GameScene.prototype.generateNumbers = function () {
        this.limit = this.numlimit[this.difficulty];
        var n = [];
        for (var i = 0; i < this.limit; i++) {
            var num = this.gm.genNumber();
            n.push(num);
        }
        //var n = Phaser.ArrayUtils.numberArray(1, this.limit);
        //Phaser.ArrayUtils.shuffle(n);
        this.numArray = n;
    };
    GameScene.prototype.resetGame = function () {
        this.curIndex = 0;
        this.gameScore = 0;
        this.totalScore = 0;
        this.currentNum = null;
        this.correctNums = [];
        this.incorrectNums = [];
        this.isCorrect = false;
        this.correct = 0;
        this.incorrect = 0;
        this.stopped = false;
        this.hitCorrect = [];
        this.hitInCorrect = [];
    };
    GameScene.prototype.validate = function (n) {
        //return n % this.multiple === 0;
        return this.gm.checknumb(this.multiple, n);
    };
    GameScene.prototype.gameOver = function () {
        this.stopped = true;
        var t = this.limit - (this.totalScore - this.correct) - this.incorrect;
        var s = Math.floor(t * 100 / this.limit);
        var correct_hit = this.hitCorrect;
        var correct_miss = this.correctNums.filter(function (obj) { return correct_hit.indexOf(obj) == -1; });
        var incorrect_hit = this.hitInCorrect;
        var incorrect_miss = this.incorrectNums.filter(function (obj) { return incorrect_hit.indexOf(obj) == -1; });
        var correct = { hit: correct_hit, miss: incorrect_miss };
        var missed = { miss: correct_miss };
        var incorrect = { hit: incorrect_hit };
        var gobj = { percent: s, correct: correct, incorrect: incorrect, missed: missed };
        //this.showEndCard()
        this.resetGame();
        this.game.state.start("GameOver", true, false, gobj);
    };
    GameScene.prototype.showEndCard = function () {
        var t = this.limit - (this.totalScore - this.correct) - this.incorrect;
        var s = Math.floor(t * 100 / this.limit) + "%";
        console.log(this.limit, this.correct, this.totalScore, this.incorrect, s);
        alert("GAME OVER:" + "\n" + "Your Score: " + s);
    };
    GameScene.prototype.updateCounter = function (n) {
        this.countertext.setText("Number Count: " + n);
    };
    GameScene.prototype.renderNumbers = function () {
        this.updateCounter(this.curIndex);
        if (this.curIndex >= this.numArray.length) {
            this.gameOver();
            return;
        }
        this.numbers = this.game.add.group();
        //enable physics in them
        this.numbers.enableBody = true;
        //phaser's random number generator
        var numL = 1;
        var num;
        for (var i = 0; i < numL; i++) {
            //add sprite within an area excluding the beginning and ending
            //  of the game world so items won't suddenly appear or disappear when wrapping
            var x = this.game.rnd.integerInRange(this.game.width, this.game.world.width - this.game.width);
            num = this.numbers.create(x, this.game.height / 3, 'numbox');
            this.currentNum = this.numArray[this.curIndex]; //this.game.rnd.integerInRange(2, 20);
            this.curIndex++;
            if (this.validate(this.currentNum)) {
                this.isCorrect = true;
                this.totalScore++;
                this.correctNums.push(this.currentNum);
                //this.settings.addPoints(1);
                console.log("CORRECT");
            }
            else {
                this.isCorrect = !true;
                this.incorrectNums.push(this.currentNum);
                console.log("INCORRECT");
            }
            var f = this.multiple == "rational" ? "30px Monospace" : "50px Monospace";
            var lab = this.game.add.text(x + 50, (this.game.height / 3) + 50, String(this.currentNum), { font: f, fill: "#000", align: "center" }, this.numbers);
            lab.anchor.set(0.5, 0.5);
            //physics properties
            num.body.velocity.x = 0;
            num.connectedto = lab;
            num.body.immovable = true;
            num.body.collideWorldBounds = false;
        }
    };
    GameScene.prototype.generateenvobj = function () {
    };
    GameScene.prototype.playerHit = function (car, item) {
        var _this = this;
        console.log("Hit:" + this.currentNum);
        if (this.isCorrect) {
            this.correct++;
            this.showFlash();
            this.removeNumber(item);
            this.hitCorrect.push(this.currentNum);
        }
        else {
            this.incorrect++;
            this.hitInCorrect.push(this.currentNum);
            //this.showInCorrect();
            //this.game.paused = true;
            this.showMsg(this.currentNum, function () { _this.removeNumber(item); });
        }
    };
    GameScene.prototype.showFlash = function (msg, duration, delay, color, cb, pause) {
        var _this = this;
        if (msg === void 0) { msg = "CORRECT!"; }
        if (duration === void 0) { duration = 500; }
        if (delay === void 0) { delay = 500; }
        if (color === void 0) { color = "#00ff00"; }
        if (cb === void 0) { cb = null; }
        if (pause === void 0) { pause = false; }
        this.messagetext.setText(msg);
        this.messagetext.addColor(color, 0);
        var c;
        var g = this.game.add.tween(this.messagetext.scale).to({ x: 1, y: 1 }, duration, "Linear", true);
        g.onComplete.add(function () { cb ? cb() : ""; if (pause) {
            _this.game.paused = true;
        } ; c = _this.game.add.tween(_this.messagetext.scale).to({ x: 0, y: 0 }, duration, "Linear", true, delay); }, this);
    };
    GameScene.prototype.removeNumber = function (item) {
        if (item.connectedto) {
            item.connectedto.destroy();
        }
        item.destroy();
    };
    GameScene.prototype.showMsg = function (n, cb) {
        var _this = this;
        var m = this.multiple;
        var msg = n + this.utils.getMText(m, 1, true); //" is not a multiple of " + m;
        this.showFlash(msg, 10, 10, "#ff0000", function () {
            setTimeout(function () {
                _this.game.paused = !true;
                cb();
            }, 2000);
        }, true);
    };
    GameScene.prototype.playerJump = function () {
        if (this.car.body.touching.down) {
            this.car.body.velocity.y -= 700;
        }
    };
    GameScene.prototype.empty = function () { };
    return GameScene;
}(Phaser.State));
var DayCycle = (function () {
    function DayCycle(game, dayLength) {
        this.game = game;
        this.dayLength = dayLength;
        this.shading = false;
        this.sunSprite = false;
        this.moonSprite = false;
    }
    DayCycle.prototype.initSun = function (sprite) {
        this.sunSprite = sprite;
        this.sunset(sprite);
    };
    DayCycle.prototype.initMoon = function (sprite) {
        this.moonSprite = sprite;
        this.moonrise(sprite);
    };
    DayCycle.prototype.initShading = function (sprites) {
        this.shading = sprites;
    };
    DayCycle.prototype.sunrise = function (sprite) {
        var _this = this;
        sprite.position.x = this.game.width - (this.game.width / 4);
        this.sunTween = this.game.add.tween(sprite.cameraOffset).to({ y: -250 }, this.dayLength, null, true);
        this.sunTween.onComplete.add(function () { _this.sunset(sprite); }, this);
        this.game.add.tween(sprite.scale).to({ x: 1, y: 1 }, this.dayLength, null, true);
        if (this.shading) {
            this.shading.forEach(function (sprite) {
                _this.tweenTint(sprite.sprite, sprite.from, sprite.to, _this.dayLength);
            });
        }
    };
    DayCycle.prototype.sunset = function (sprite) {
        var _this = this;
        sprite.position.x = 50;
        this.sunTween = this.game.add.tween(sprite.cameraOffset).to({ y: this.game.world.height }, this.dayLength, null, true);
        this.sunTween.onComplete.add(function () { _this.sunrise(sprite); }, this);
        this.game.add.tween(sprite.scale).to({ x: 2, y: 2 }, this.dayLength, null, true);
        if (this.shading) {
            this.shading.forEach(function (sprite) {
                _this.tweenTint(sprite.sprite, sprite.to, sprite.from, _this.dayLength);
            });
        }
    };
    DayCycle.prototype.moonrise = function (sprite) {
        var _this = this;
        sprite.position.x = this.game.width - (this.game.width / 4);
        this.moonTween = this.game.add.tween(sprite.cameraOffset).to({ y: -350 }, this.dayLength, null, true);
        this.moonTween.onComplete.add(function () { _this.moonset(sprite); }, this);
    };
    DayCycle.prototype.moonset = function (sprite) {
        var _this = this;
        sprite.position.x = 50;
        this.moonTween = this.game.add.tween(sprite.cameraOffset).to({ y: this.game.world.height }, this.dayLength, null, true);
        this.moonTween.onComplete.add(function () { _this.moonrise(sprite); }, this);
    };
    DayCycle.prototype.tweenTint = function (spriteToTween, startColor, endColor, duration) {
        var colorBlend = { step: 0 };
        this.game.add.tween(colorBlend).to({ step: 100 }, duration, Phaser.Easing.Default, false)
            .onUpdateCallback(function () {
            spriteToTween.tint = Phaser.Color.interpolateColor(startColor, endColor, 100, colorBlend.step, 1);
        })
            .start();
    };
    return DayCycle;
}());
window.onload = function () {
    var gameInst = new SimpleGame();
};
var Utils = (function () {
    function Utils() {
        if (Utils._instance) {
            throw new Error("Error: Instantiation failed: Use Utils.getInstance() instead of new.");
        }
        Utils._instance = this;
    }
    Utils.getInstance = function () {
        return Utils._instance;
    };
    Utils.prototype.pushUnique = function (arr, item) {
        if (arr.indexOf(item) == -1) {
            //if(jQuery.inArray(item, this) == -1) {
            arr.push(item);
            return true;
        }
        return false;
    };
    Utils.prototype.uniqueArr = function (arr) {
        var narr = [];
        for (var i = 0; i < arr.length; i++) {
            this.pushUnique(narr, arr[i]);
        }
        return narr;
    };
    Utils.prototype.makeUnique = function (arr) {
        var narr = [];
        for (var i = 0; i < arr.length; i++) {
            this.pushUnique(narr, arr[i]);
        }
        arr = narr;
    };
    Utils.prototype.getMText = function (m, l, boo) {
        var txt;
        if (!isNaN(m)) {
            txt = l > 1 ? "multiples of " + m : "a multiple of " + m;
            if (boo) {
                txt = l > 1 ? " are not " + txt : " is not " + txt;
            }
            else {
                txt = l > 1 ? " are " + txt : " is " + txt;
            }
        }
        else {
            if (m === 'prime') {
            }
            else if (m === 'sq') {
                m = "perfect square";
            }
            else if (m == '') { }
            txt = l > 1 ? m + "s" : "a " + m;
            if (boo) {
                txt = l > 1 ? " are not " + txt : " is not " + txt;
            }
            else {
                txt = l > 1 ? " are " + txt : " is " + txt;
            }
        }
        return txt;
    };
    //------------function that decides the frequency of multiples(numbers) appearing------//
    //---------------------and to create different numbers for different levels------------//
    Utils.prototype.myrandom = function (n, l) {
        var my_array = new Array();
        var r1, r, nu, s, rand;
        if (l == 1) {
            r1 = this.non(27, n);
            if (n == "prime") {
                r = this.prime(27);
            }
            else if (n == "sq") {
                r = this.psqr(5);
            }
            else {
                r = (this.random(Math.round(r1 / n)) * n);
            }
            if (r == 0) {
                r = (Math.round(r1 / n) + 1) * n;
            }
        }
        if (l == 2) {
            r1 = this.non(57, n);
            if (n == "prime") {
                r = this.prime(56);
            }
            else if (n == "sq") {
                r1 = this.random(98) + 1;
                r = this.psqr(10);
            }
            else {
                r = (this.random(Math.round(r1 / n)) * n);
            }
            if (r == 0) {
                r = (Math.round(r1 / n) + 1) * n;
            }
        }
        if (l == 3) {
            r1 = this.non(153, n);
            if (n == "prime") {
                r = this.prime(99);
            }
            else if (n == "sq") {
                r1 = this.random(223) + 1;
                r = this.psqr(15);
            }
            else if (n == 2 || n == 3 || n == 4 || n == 5) {
                r1 = this.non(200, n);
                r = (this.random(Math.round(r1 / n))) * n;
            }
            else if (n == 7 || n == 8) {
                r1 = this.non(126, n);
                r = (this.random(Math.round(r1 / n))) * n;
            }
            else {
                r = (this.random(Math.round(r1 / n))) * n;
            }
            if (r == 0) {
                r = (Math.round(r1 / n) + 1) * n;
            }
        }
        if (n == "prime") {
            my_array[0] = r1;
            my_array[1] = r;
            my_array[2] = r1;
            my_array[3] = r1;
            nu = 4;
        }
        else if (n == "sq") {
            my_array[0] = r1;
            my_array[1] = r;
            my_array[2] = r1;
            my_array[3] = r1;
            nu = 4;
        }
        else {
            for (var i = 0; i < n; i++) {
                if (i % 2 == 0) {
                    my_array[i] = r;
                }
                else {
                    my_array[i] = r1;
                }
            }
            nu = n;
        }
        s = this.random(nu);
        rand = my_array[s];
        //trace(my_array+":"+rand);
        return rand;
    };
    //------------------function  to create prime-----------------------------------------//
    Utils.prototype.prime = function (l) {
        var b, cnt, p;
        do {
            b = this.random(l) + 1;
            cnt = 0;
            for (p = 1; p <= b; p++) {
                if (b % p == 0) {
                    cnt++;
                }
            }
        } while (cnt > 2);
        return b;
    };
    //------------------function  to create perfect squares-------------------------------//
    Utils.prototype.psqr = function (l) {
        var s;
        s = this.random(l) + 1;
        s = Math.pow(s, 2);
        return s;
    };
    //------------------function  to check whether a number is prime----------------------//
    Utils.prototype.isprime = function (chk) {
        var enter, cnt, result, var_p, var_a;
        enter = chk;
        cnt = 0;
        for (var p = 1; p <= enter; p++) {
            if (enter % p == 0) {
                cnt++;
            }
        }
        if (cnt == 2) {
            result = true;
        }
        else {
            for (var p = 2; p < enter; p++) {
                if (enter % p == 0) {
                    break;
                }
            }
            var_p = p;
            var_a = chk / var_p;
            result = false;
        }
        return result;
    };
    //------------------function  to check whether a number is perfect sqr----------------//
    Utils.prototype.ispsqr = function (myNumb) {
        var Integer;
        var root = Math.sqrt(myNumb);
        if (root == parseInt(String(root))) {
            Integer = true;
        }
        else {
            Integer = false;
        }
        return Integer;
    };
    Utils.prototype.checknumb = function (n, numb) {
        var re;
        var temp;
        if (n == "prime") {
            re = this.isprime(numb);
        }
        else if (n == "sq") {
            re = this.ispsqr(numb);
        }
        else if (n == "rational") {
            re = this.rational(numb);
        }
        else if (n == "fracLT") {
            temp = this.fracToLT(numb);
            re = (temp == numb);
        }
        else {
            re = (numb % n == 0);
        }
        return re;
    };
    Utils.prototype.random = function (s) {
        return Math.floor((Math.random() * s));
    };
    Utils.prototype.non = function (l, d) {
        var b;
        do {
            b = this.random(l) + 1;
        } while (b % d == 0);
        return b;
    };
    //------------------------------------------****---------------------------------------//
    Utils.prototype.isRational = function (str) {
        if (String(str).indexOf("√") != -1) {
            var myNumb = Number(str.split("√")[1]);
            return this.ispsqr(myNumb);
        }
        if (String(str).indexOf("π") != -1) {
            var count = String(str).split("π").length;
            if (count == 3) {
                return true;
            }
            else {
                return false;
            }
        }
        return true;
    };
    //------------------------------------------****---------------------------------------//
    Utils.prototype.rational = function (numb) {
        numb = String(numb);
        if (numb.indexOf("√") > -1) {
            var myNumb = Number(numb.split("√")[1]);
            return this.ispsqr(myNumb);
        }
        if (String(numb).indexOf("π") != -1) {
            var count = String(numb).split("π").length;
            if (count == 3) {
                return true;
            }
            else {
                return false;
            }
        }
        if (String(numb).indexOf("/") != -1) {
            var str = String(numb).split("/");
            var rNumero = this.isRational(str[0]);
            var rdenom = this.isRational(str[1]);
            if (rNumero && rdenom) {
                return true;
            }
            else {
                return false;
            }
        }
        return true;
    };
    Utils.prototype.getGCD = function () {
        var arg = [];
        for (var _i = 0; _i < arguments.length; _i++) {
            arg[_i] = arguments[_i];
        }
        var argL = arg.length;
        var gcd;
        //trace(arguments[0]+":"+arguments[1])
        if (argL < 2) {
            var mystr = String(arg[0]);
            if (!isNaN(mystr)) {
                return arg[0];
            }
            else {
                if (mystr.indexOf("/") == -1) {
                    return arg[0];
                }
                else {
                    var myvars = mystr.split("/");
                    var n = myvars[0];
                    var d = myvars[1];
                    return this.getGCD(n, d);
                }
            }
        }
        else if (argL > 2) {
            return null;
        }
        else {
            if (arg[1] == 0) {
                return arg[0];
            }
            else {
                return this.getGCD(arg[1], arg[0] % arg[1]);
            }
        }
    };
    Utils.prototype.fracToLT = function () {
        var arg = [];
        for (var _i = 0; _i < arguments.length; _i++) {
            arg[_i] = arguments[_i];
        }
        var argL = arg.length;
        var gcd, flt;
        gcd = this.getGCD(arg);
        if (argL < 2) {
            var mystr = String(arg[0]);
            if (!isNaN(mystr)) {
                return arg[0];
            }
            else {
                if (mystr.indexOf("/") == -1) {
                    return null;
                }
                else {
                    var myvars = mystr.split("/");
                    var n = myvars[0];
                    var d = myvars[1];
                    var num = (n / gcd);
                    var den = (d / gcd);
                    if (Number(den) == 1 || Number(num) == Number(den)) {
                        flt = num;
                    }
                    else {
                        flt = num + "/" + den;
                    }
                    //flt = (n/gcd)+"/"+(d/gcd);
                    return flt;
                }
            }
        }
        else if (argL > 2) {
            return null;
        }
        else {
            var n = arg[0];
            var d = arg[1];
            if (d == 0) {
                return n;
            }
            else {
                var num = (n / gcd);
                var den = (d / gcd);
                if (Number(den) == 1 || Number(num) == Number(den)) {
                    flt = num;
                }
                else {
                    flt = num + "/" + den;
                }
                //flt = (n/gcd)+"/"+(d/gcd);
                return flt;
            }
        }
    };
    return Utils;
}());
Utils._instance = new Utils();
var GameManager = (function () {
    function GameManager(lvl, type) {
        this.tl = 0;
        this.count = 50;
        this.negSign = [1, -1, 1, 1, -1];
        this.numTypes = ["sqrt", "sqrt", "sqrt", "pi", "integers", "integers", "integers", "integers", "integers", "integers"];
        this.level = lvl;
        this.multiple = type;
        this.utils = Utils.getInstance();
    }
    GameManager.prototype.genMixedNumbers = function (limit) {
        var numTypes = this.numTypes;
        var negSign = this.negSign;
        //let random = this.utils.random;
        //let psqr = this.utils.psqr;
        var numero, denom, numb;
        var type = numTypes[this.utils.random(numTypes.length)];
        if (type == "pi") {
            if (this.utils.random(2)) {
                numero = (this.utils.random(limit - 2) + 2) + "π";
                denom = (this.utils.random(10) + 1) + ["", "π"][this.utils.random(2)];
                numero = numero == "1π" ? "π" : numero;
                denom = denom == "1π" ? "π" : denom;
                numb = (numero == denom) ? numero : (numero + "/" + denom);
            }
            else {
                numero = (this.utils.random(10) + 1) + "π";
                numero = numero == "1π" ? "π" : numero;
                numb = numero;
            }
        }
        if (type == "sqrt") {
            var rElem = [this.utils.psqr(10), this.utils.psqr(10), this.utils.psqr(10), this.utils.psqr(10), (this.utils.random(limit) + 2)];
            numero = "√" + (rElem[this.utils.random(rElem.length)]);
            numb = numero;
        }
        if (type == "integers") {
            var prob = [1, 1, 1, 0, 0, 1, 1, 0, 1, 1, 1];
            if (prob[this.utils.random(prob.length)]) {
                numero = (this.utils.random(limit) + 1) * negSign[this.utils.random(negSign.length)];
                denom = ["", "", "", "", "√"][this.utils.random(5)] + (this.utils.random(limit - 2) + 2);
                numb = (numero == denom) ? numero : (numero + "/" + denom);
            }
            else {
                numero = this.utils.random(limit) + 1;
                numb = numero;
            }
        }
        console.log("Rational:" + type, numb);
        return numb;
    };
    GameManager.prototype.checknumb = function (n, numb) {
        var re;
        var temp;
        if (n == "prime") {
            re = this.utils.isprime(numb);
        }
        else if (n == "sq") {
            re = this.utils.ispsqr(numb);
        }
        else if (n == "rational") {
            re = this.utils.rational(numb);
        }
        else if (n == "fracLT") {
            temp = this.utils.fracToLT(numb);
            re = (temp == numb);
        }
        else {
            re = (numb % n == 0);
        }
        return re;
    };
    GameManager.prototype.genNumber = function () {
        var count = this.count;
        var tl = this.tl;
        var limit = this.limit;
        var temp_n, temp_d, numb;
        var lvl = this.multiple;
        if (this.multiple == "rational" || this.multiple == "fracLT") {
            var a0 = {};
            switch (this.level) {
                case 1:
                    limit = 20;
                    break;
                case 2:
                    limit = 50;
                    break;
                case 3:
                    limit = 100;
                    break;
            }
            if (this.multiple == "fracLT") {
                //numb_n = myrandom(lvl, this.level);
                a0.numb_n = this.utils.random(limit) + 1;
                a0.numb_d = this.utils.random(limit - 2) + 2;
                temp_n = a0.numb_n;
                temp_d = a0.numb_d;
                a0.numb_n = Math.min(temp_n, temp_d);
                a0.numb_d = Math.max(temp_n, temp_d);
                numb = a0.numb_n + "/" + a0.numb_d;
            }
            else if (this.multiple == "rational") {
                numb = this.genMixedNumbers(limit);
            }
        }
        else {
            numb = this.utils.myrandom(lvl, this.level);
        }
        return numb;
    };
    return GameManager;
}());
//# sourceMappingURL=app.js.map
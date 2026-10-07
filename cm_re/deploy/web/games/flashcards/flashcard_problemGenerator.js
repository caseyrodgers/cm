var FC_ProblemGenerator = function(id, opts) {
    var fcpg = this;
    fcpg.options = $.extend({
        id: id,
        difficulty: 'low'
    }, opts);
    fcpg.getProblems = function() {
        var data = this.getjson('./data/' + fcpg.options.id + ".json");
        fcpg.data = JSON.parse(data);
        fcpg.problems_list = fcpg.data.problems;
        fcpg.problems = fcpg.problems_list[fcpg.options.difficulty];
        var help_data = this.getjson('./data/' + fcpg.options.id + "-help.json");
        fcpg.help_data = JSON.parse(help_data);
        console.log(fcpg.problems);
    }
    fcpg.getjson = function(file) {
        return $.ajax({
            type: "GET",
            url: file,
            dataType: "json",
            async: false
        }).responseText;

    }
}
var FC_ProblemRenderer = (function() {
    var fcpr = {};
    fcpr.renderTitle = function(cont, data) {
        var title = data.title;
        //var stat=data.statement;
        var tblock = $(cont).find(".fc_title");
        // var sblock=$(cont).find(".fc_quest_statement span");
        tblock.html(title);
        // sblock.html(stat);
    };
    fcpr.renderQInfo = function(cont, i, l) {
        var info = $(cont).find(".fc_quest_info .info_cont");
        var s = "<span class='fc_qinfo_count'>Question: " + i + " of " + l + "</span><br><span class='fc_qinfo_tip'> You will get your score on completion of " + l + " problems</span>";
        info.html(s);
    }
    fcpr.renderProbStatement = function(cont, data) {
        //var title=data.title;
        var stat = data.statement;
        //var tblock=$(cont).find(".fc_title");
        var sblock = $(cont).find(".fc_quest_statement span");
        //tblock.html(title);
        sblock.html(stat);
        var iblock = $(cont).find(".quest_inputblock");
        var params = data.global_params;
        var type = params.type;
        var _html = getNormalblock('0');
        if (type == 'rational') {
            _html=getFractionField('0',80,'fraction');
        }
        iblock.html(_html);
    }
    fcpr.parse=function(str){
    return asciimath.parseMath(str);
    }
    fcpr.render = function(cont, data, opts) {
        var question_data = data["display_question"];
        var qblock = $(cont).find(".quest_leftblock");
        var eqblock = $(cont).find(".quest_eqblock");

        qblock.html(fcpr.parse(question_data));
        eqblock.html("=");
    }
    fcpr.renderHelp = function(cont, data, qdata, opts) {

        var sdata = Object.keys(qdata.step_params);
        var sdstr = sdata.join(",");
        var doLT = sdstr.indexOf("$factor") > -1;
        var doSimplify = sdstr.indexOf("$answer_in_lowestterms") > -1;
        if ($(cont).is(":visible")) {
            cont.hide();
            return
        }
        $(cont).empty();
        for (var i = 0; i < data.length; i++) {
            var line = data[i];
            var type = line.type;
            var value = line.value;
            if (i == 3) {
                if (!doLT) {
                    break;
                }
            }
            if (i == 5) {
                if (!doSimplify) {
                    break;
                }
            }
            value = fcpr.parseToken(value, qdata);
            if (type == "question") {
                $(cont).append($("<div name='line_block_'" + i + " class='question'></div>").html(fcpr.parse(qdata["display_question"])))
            } else if (type == "text") {
                $(cont).append("<div name='line_block_'" + i + " class='textLine'>" + value + "</div>")
            } else if (type == 'equation') {

                var pvalue = fcpr.getEquation(value, qdata, opts)
                $(cont).append($("<div name='line_block_'" + i + " class='eqnLine'></div>").html(pvalue))
            }

        }
        showHelp();
    }
    fcpr.getEquation = function(value, qdata, opts) {

        return fcpr.parse(value);
    }
    fcpr.parseToken = function(value, qdata) {
        var sdata = Object.keys(qdata.step_params);
        for (var m = 0; m < sdata.length; m++) {
            var key = sdata[m];
            value = value.replace(eval("/\\" + key + "/g"), qdata.step_params[key]);

        }
        return value;
    }
    return fcpr;
})();
var FC_Manager = function(cont, id, opts) {
    var fcm = this;
    fcm.options = {};
    if (cont) {
        fcm.container = cont;
    }
    if (id) {
        fcm.id = id;
    }
    if (opts) {
        fcm.options = $.extend(fcm.options, opts);
    }
    fcm.problemIndex = 0;

    fcm.init = function(cont, id, opts) {
        if (cont) {
            fcm.container = cont;
        }
        if (id) {
            fcm.id = id;
        }
        if (opts) {
            fcm.options = $.extend(fcm.options, opts);
        }
        fcm.problemLimit = fcm.options.limit ? fcm.options.limit : 10;
        fcm.problemgenerator = new FC_ProblemGenerator(fcm.id, fcm.options);
        fcm.problemgenerator.getProblems();
        FC_ProblemRenderer.renderProbStatement(fcm.container, fcm.problemgenerator.data);
        fcm.helpData = fcm.problemgenerator.help_data;
        fcm.showProblem();
    }
    fcm.getNextProblem = function() {
        var src = fcm.problemgenerator.problems;
        var prob = src[fcm.problemIndex];
        fcm.problemIndex++;
        if (fcm.problemIndex >= fcm.problemLimit) {
            fcm.problemIndex = 0;
            fcm.problemgenerator.getProblems();
        }
        return prob;
    }
    fcm.getProblem = function(i) {
        var src = fcm.problemgenerator.problems;
        var prob = i === undefined ? fcm.getNextProblem() : src[i];
        return prob;
    }
    fcm.showProblem = function() {
        var pi = fcm.problemIndex
        fcm.currentProblem = fcm.getNextProblem();
        FC_ProblemRenderer.render(fcm.container, fcm.currentProblem, fcm.options);
        FC_ProblemRenderer.renderQInfo(fcm.container, pi + 1, fcm.problemLimit);
    }
    fcm.showHelp = function() {
        FC_ProblemRenderer.renderHelp(fcm.options.helpCont, fcm.helpData, fcm.currentProblem, fcm.options);
    }
}
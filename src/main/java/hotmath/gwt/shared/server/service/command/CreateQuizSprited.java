package hotmath.gwt.shared.server.service.command;

import hotmath.cm.util.CmWebResourceManager;
import hotmath.sprite.SpriteWriter;

import java.io.File;

import sb.util.SbFile;



class CreateQuizSprited {
    File quizSprited;
    String quizHtml;
    public CreateQuizSprited(File quizSprited, String quizHtml) {
        this.quizSprited = quizSprited;
        this.quizHtml = quizHtml;
    }
    public void createQuizSpritedHtml() throws Exception {

        String locationOfSprite = "/" + CmWebResourceManager.getInstance().getRetailedWebBase() + "/" + quizSprited.getName();
        String spritedHtml = new SpriteWriter().processHtml(quizSprited, locationOfSprite,quizHtml);
        
        SbFile file = new SbFile(new File(quizSprited, "tutor_steps-sprited.html"));
        file.setFileContents(spritedHtml);
        file.writeFileOut();
    }
}
package me.oneqxz.cashsystem.report;

import lombok.Getter;
import lombok.extern.log4j.Log4j2;
import me.oneqxz.cashsystem.report.sections.AffectedScreenSection;
import me.oneqxz.cashsystem.report.sections.ICrashSection;
import me.oneqxz.cashsystem.report.sections.StackTraceCrushSection;
import me.oneqxz.cashsystem.report.sections.SystemDetailsSection;
import me.sgx.GeometryDash;
import org.apache.commons.io.IOUtils;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;

@Log4j2
@Getter
public class CrashReport {
    private final String message;
    private final Throwable cause;
    private final ICrashSection[] sections = new ICrashSection[]{
            new StackTraceCrushSection(),
            new AffectedScreenSection(),
            new SystemDetailsSection()
    };

    public CrashReport(String message, Throwable cause) {
        this.message = message;
        this.cause = cause;
    }

    private String getCauseAsString() {
        String string;
        StringWriter stringWriter = null;
        PrintWriter printWriter = null;
        Throwable throwable = getThrowable();
        try {
            stringWriter = new StringWriter();
            printWriter = new PrintWriter(stringWriter);
            throwable.printStackTrace(printWriter);
            string = stringWriter.toString();
        } catch (Throwable throwable2) {
            IOUtils.closeQuietly(stringWriter);
            IOUtils.closeQuietly(printWriter);
            throw throwable2;
        }
        IOUtils.closeQuietly(stringWriter);
        IOUtils.closeQuietly(printWriter);
        return string;
    }

    private Throwable getThrowable() {
        Throwable throwable = this.cause;
        if (throwable.getMessage() == null) {
            if (throwable instanceof NullPointerException) {
                throwable = new NullPointerException(this.message);
            } else if (throwable instanceof StackOverflowError) {
                throwable = new StackOverflowError(this.message);
            } else if (throwable instanceof OutOfMemoryError) {
                throwable = new OutOfMemoryError(this.message);
            }
            throwable.setStackTrace(this.cause.getStackTrace());
        }
        return throwable;
    }

    public String toString()
    {
        StringBuilder sb = new StringBuilder();
        sb.append("---- Geometry Dash v%s Crash Report ----\n".formatted(GeometryDash.VERSION.getAsString()));
        sb.append("// %s\n\n".formatted(this.generateWittyComment()));

        sb.append("Time: %s\n".formatted(this.getTime()));
        sb.append("Description: %s\n\n".formatted(this.message));

        sb.append(this.getCauseAsString());
        sb.append("\n\nA detailed walkthrough of the error, its code path and all known details is as follows:\n");
        sb.append("-".repeat(87));
        sb.append("\n\n");
        sb.append(this.getStackTraceAsString());

        return sb.toString();
    }

    private String getStackTraceAsString()
    {
        StringBuilder sb = new StringBuilder();
        for(ICrashSection section : this.sections)
        {
            sb.append(section.formatSection(this.cause));
            sb.append("\n\n");
        }
        return sb.toString();
    }

    private String getTime()
    {
        return new SimpleDateFormat().format(new Date());
    }

    private String generateWittyComment()
    {
        String[] comments = new String[]{
                "Who set up this spike trap?",
                "Everything's going according to plan. No, really, that was supposed to happen.",
                "Uh... Did I do that?",
                "Oops.",
                "Why did you do that?",
                "I feel sad now :(",
                "My bad.",
                "I'm sorry, Dave.",
                "I let you down. Sorry :(",
                "On the bright side, I bought you a plushie!",
                "Daisy, daisy...",
                "Oh - I know what I did wrong!",
                "Hey, that tickles! Hehehe!",
                "I blame RobTop.",
                "You should try our sister game, Geometry Dash!",
                "Don't be sad. I'll do better next time, I promise!",
                "Don't be sad, have a hug! <3",
                "I just don't know what went wrong :(",
                "Shall we play a game?",
                "Quite honestly, I wouldn't worry myself about that.",
                "I bet aliens wouldn't have this problem.",
                "Sorry :(",
                "Surprise! Haha. Well, this is awkward.",
                "Would you like a cupcake?",
                "Hi. I'm Geometry Dash, and I'm a crashaholic.",
                "Ooh. Shiny.",
                "This doesn't make any sense!",
                "Why is it breaking :(",
                "Don't do that.",
                "Ouch. That hurt :(",
                "You're mean.",
                "This is a token for 1 free hug. Redeem at your nearest RobTop's place: [HUG]",
                "There are four lights!",
                "But it works on my machine."
        };
        try {
            return comments[new Random().nextInt(comments.length-1)];
        } catch (Throwable throwable) {
            return "Witty comment unavailable :(";
        }
    }

    public static CrashReport create(Throwable cause, String title)
    {
        return new CrashReport(title, cause);
    }

}

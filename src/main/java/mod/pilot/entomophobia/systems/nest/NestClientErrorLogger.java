package mod.pilot.entomophobia.systems.nest;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nullable;

/**
 * Current unused due to dependant code requiring access to nests on the client side :/
 */
@OnlyIn(Dist.CLIENT)
@Deprecated
public class NestClientErrorLogger {
    private static final String loggerPrepend = "[NEST CLIENT ERROR LOGGER]";
    public static void throwDefaultError(){
        NestClientErrorLogger.throwError(loggerPrepend + "[DEFAULT MESSAGE] ERROR! Attempted to access nests on client side! See stacktrace for source.");
    }
    public static void throwEmptyError(){
        throwError(null, null);
    }
    public static void throwError(String msg){
        throwError(msg, null);
    }
    public static void throwError(Throwable cause){
        throwError(null, cause);
    }
    public static void throwError(@Nullable String msg, @Nullable Throwable cause){
        if (msg != null && !msg.startsWith(loggerPrepend)) msg = loggerPrepend + msg;

        NestClientAccessError err;
        boolean msgFlag = msg != null;
        boolean causeFlag = cause != null;
        if (msgFlag){
            if (causeFlag) {
                err = new NestClientAccessError(msg, cause);
            } else err = new NestClientAccessError(msg);
        } else if (causeFlag) err = new NestClientAccessError(cause);
        else err = new NestClientAccessError();
        throw err;
    }
    private static class NestClientAccessError extends Error{
        protected NestClientAccessError(){super();}
        protected NestClientAccessError(String msg){super(msg);}
        protected NestClientAccessError(Throwable cause){super(cause);}
        protected NestClientAccessError(String msg, Throwable cause){super(msg, cause);}
    }
}

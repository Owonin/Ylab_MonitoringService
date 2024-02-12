package auth;

import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Фабрика сессий
 */
public class AuthContextFactory {
    private static final Map<String, AuthContext> contextMap = new HashMap<>();
    private static final long CONTEXT_EXPIRATION_TIME = 30 * 60 * 1000; // 30 минут
    private static final Timer timer = new Timer();

    /**
     * Таймер удаления контекстов авторизации
     *
     * @param userId id сессии пользователя
     */
    private static void scheduleContextExpiration(final String userId) {
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                contextMap.remove(userId);
            }
        }, CONTEXT_EXPIRATION_TIME);
    }

    /**
     * Получение контекста авторизации по id сессии
     *
     * @param userId id сессии пользователя
     * @return
     */
    public AuthContext getAuthContextForUser(String userId) {
        if (!contextMap.containsKey(userId)) {
            AuthContext authContext = new AuthContext();
            contextMap.put(userId, authContext);
            scheduleContextExpiration(userId);
            return authContext;
        }
        return contextMap.get(userId);
    }
}

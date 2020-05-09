package net.cyberkitsune.prefixchat;


import net.cyberkitsune.prefixchat.channels.KitsuneChannel;
import net.cyberkitsune.prefixchat.tags.ChatTag;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.reflections.Reflections;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.regex.Pattern;

import static java.util.regex.Pattern.compile;

public class MessageTagger {
    private static MessageTagger instance;
    private HashMap<String, Class<? extends ChatTag>> taggers;

    private MessageTagger()
    {
        taggers = new HashMap<>();
        loadTaggers();
    }

    private void loadTaggers()
    {
        // Grab everything that implements ChatTag
        Reflections reflections = new Reflections("net.cyberkitsune.prefixchat.tags");
        for(Class<? extends ChatTag> tagClass : reflections.getSubTypesOf(ChatTag.class))
        {
            // Inst
            String placeholders = "";
            try {
                ChatTag ct = tagClass.getDeclaredConstructor().newInstance();
                placeholders = ct.getPlaceholder();
            } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
                KitsuneChat.getInstance().mcLog.warning(String.format("Unable to load ChatTag %s", tagClass.getSimpleName()));
                e.printStackTrace();
            }
            if (placeholders.isEmpty())
                continue;

            String[] holders = placeholders.split(",");
            for (String p_holder : holders)
            {
                taggers.put(p_holder, tagClass);
            }

        }
    }

    public String formatMessage(String format, String message, KitsuneChannel channel, AsyncPlayerChatEvent context)
    {
        // Step 1 -- Split format into tag array
        String[] parts = format.split("((?<=})|(?=\\{))");

        ArrayList<String> formatted_message = new ArrayList<>();
        for(String part : parts)
        {
            Pattern tagCheck = Pattern.compile("\\{.*}");
            if(tagCheck.matcher(part).matches())
            {
                String just_placeholder = part.substring(1, part.length() - 1);
                if (taggers.containsKey(just_placeholder))
                {
                    Class<? extends ChatTag> tagClass = taggers.get(just_placeholder);
                    try {
                        ChatTag tagFormatter = tagClass.getDeclaredConstructor().newInstance();
                        formatted_message.add(tagFormatter.getReplacement(message, channel, context, just_placeholder));
                    } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
                        KitsuneChat.getInstance().mcLog.warning(String.format("Unable to create ChatTag %s", tagClass.getSimpleName()));
                        e.printStackTrace();
                    }
                }
            }
            else
            {
                formatted_message.add(part);
            }
        }
        return KitsuneChatUtils.getInstance().colorizeString(String.join("", formatted_message));
    }

    public static MessageTagger getInstance()
    {
        if(instance == null)
        {
            instance = new MessageTagger();
        }

        return instance;
    }
}

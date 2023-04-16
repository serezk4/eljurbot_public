package com.serezka.eljurbot.telergam.bot;

import com.serezka.eljurbot.Lang;
import com.serezka.eljurbot.db.UserRoles;
import com.serezka.eljurbot.db.services.UserService;
import com.serezka.eljurbot.telergam.bot.commands.Command;
import com.serezka.eljurbot.telergam.bot.session.MenuSessionManager;
import com.serezka.eljurbot.telergam.bot.session.StepSessionManager;
import com.serezka.eljurbot.telergam.bot.session.type.menu.MenuSession;
import com.serezka.eljurbot.telergam.bot.util.MessageUtil;
import com.serezka.eljurbot.telergam.bot.util.UpdateUtil;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.java.Log;
import org.springframework.stereotype.Controller;

import java.util.*;

@Log
@Controller
@RequiredArgsConstructor
public class TelegramHandler {
    private final List<Command> commands = new ArrayList<>();

    final UserService userService;

    public void addCommand(Command command) {
        commands.add(command);
    }

    @SneakyThrows
    public void process(TelegramBot tgBot, TelegramUpdate tgUpdate) {
        if (tgUpdate.getSelf().hasMessage() && tgUpdate.getSelf().getMessage().hasSticker()) log.info("sent sticker: " + tgUpdate.getSelf().getMessage().getSticker().getFileId());
        if (tgUpdate.getSelf().getMessage() != null && !(tgUpdate.getSelf().getMessage().getText() == null || tgUpdate.getSelf().getMessage().getWebAppData() == null)) return;

        // -> collect data
        String chatId = tgUpdate.getChatId();
        String username = tgUpdate.getUsername();
        String text = tgUpdate.getText();
        int messageId = tgUpdate.getMessageId();

        log.info(String.format("New Message: chatId[%s] username[%s] message[%s]", chatId, username, text));

        boolean admin = false;
        if (userService.findUserByChatId(chatId) != null) admin = userService.findUserByChatId(chatId).getRole().equals(UserRoles.ADMIN.getName());

        if (text.equals(Lang.cancel_short) || text.equals(Lang.cancel)) {
            if (StepSessionManager.containsSession(chatId)) {
                StepSessionManager.getSession(chatId).removeMessages(tgBot);
                StepSessionManager.removeSession(chatId);
            }

            tgBot.execute(MessageUtil.sendMessage(chatId, Lang.operation_canceled));
            return;
        }

        // check sessions
        if (tgUpdate.getSelf().hasCallbackQuery() && MenuSessionManager.containsSession(chatId, tgUpdate.getSelf().getCallbackQuery().getData().split(";")[0])) {
            MenuSessionManager.getSession(chatId, tgUpdate.getSelf().getCallbackQuery().getData().split(";")[0]).process(tgBot,tgUpdate);
            return;
        }

        if (StepSessionManager.containsSession(chatId)) {
            StepSessionManager.getSession(chatId).process(tgBot, tgUpdate);
            return;
        }

        // find command
        List<Command> filteredCommands = commands.stream().filter(command -> command.getNames().stream().anyMatch(commandName -> text.trim().startsWith(commandName))).toList();

        if (filteredCommands.isEmpty())
            tgBot.sendMessage(chatId, getHelp(admin));
        else {
            Command selected = filteredCommands.get(0);
            if (selected.isAdmin()) {
                if (admin) {
                    if (selected.getStepGenerators() == null && selected.getMenu() == null) selected.execute(tgBot, tgUpdate, Collections.emptyList());
                    else if (selected.getStepGenerators() != null) {
                        StepSessionManager.addSession(chatId, selected);
                        StepSessionManager.getSession(chatId).process(tgBot, tgUpdate);
                    } else {
                        MenuSessionManager.addSession(new MenuSession(chatId, selected.getMenu())).process(tgBot, tgUpdate);
                    }
                } else tgBot.sendSticker(chatId, Lang.TELEGRAM_HANDLER_ACCESS_DENIED);
            } else {
                if (selected.getStepGenerators() == null && selected.getMenu() == null) selected.execute(tgBot, tgUpdate, Collections.emptyList());
                else if (selected.getStepGenerators() != null) {
                    StepSessionManager.addSession(chatId, selected);
                    StepSessionManager.getSession(chatId).process(tgBot, tgUpdate);
                } else {
                    MenuSessionManager.addSession(new MenuSession(chatId, selected.getMenu())).process(tgBot, tgUpdate);
                }
            }
        }
    }

    private String getHelp(boolean admin) {
        StringBuilder helpBuilder = new StringBuilder();
        commands.forEach(command -> {
            if (command.isAdmin() && admin)
                helpBuilder.append(String.format(Lang.command_help_amd, command.getNames(), command.getHelp())).append("\n");
            else if (!command.isAdmin())
                helpBuilder.append(String.format(Lang.command_help, command.getNames(), command.getHelp())).append("\n");
        });
        return helpBuilder.toString();
    }
}

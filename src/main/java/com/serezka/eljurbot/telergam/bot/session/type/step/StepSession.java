package com.serezka.eljurbot.telergam.bot.session.type.step;

import com.serezka.eljurbot.telergam.bot.TelegramBot;
import com.serezka.eljurbot.telergam.bot.TelegramUpdate;
import com.serezka.eljurbot.telergam.bot.commands.Command;
import com.serezka.eljurbot.telergam.bot.util.MessageUtil;
import com.serezka.eljurbot.telergam.bot.util.UpdateUtil;
import com.serezka.eljurbot.telergam.bot.session.StepSessionManager;
import lombok.SneakyThrows;
import lombok.extern.java.Log;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.ParseMode;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;

import java.util.*;

@Log
public class StepSession {
    private final Iterator<StepGenerator> stepGeneratorIterator;

    private final List<String> data = new ArrayList<>();

    private final String chatId;
    private final Command command;

    private final Set<Integer> messagesId = new HashSet<>();

    public StepSession(Command command, String chatId) {
        this.stepGeneratorIterator = command.getStepGenerators().listIterator();
        this.chatId = chatId;
        this.command = command;
    }

    public void process(TelegramBot tgBot, TelegramUpdate tgUpdate) {
        // add input text to data array
        data.add(tgUpdate.getText());

        // if all steps finished -> execute command
        if (!stepGeneratorIterator.hasNext()) {
            data.remove(0);
            command.execute(tgBot, tgUpdate, data);
            StepSessionManager.removeSession(chatId);
            removeMessages(tgBot);
            return;
        }

        // get next step and execute
        Step currentStep = stepGeneratorIterator.next().get(chatId, data);

        // check if we can skip step
        String skipText = currentStep.canSkip(chatId, data);
        if (!skipText.equals("")) {
            data.add(skipText);
            process(tgBot, tgUpdate);
            return;
        }

        // -> generate send message
        if (tgUpdate.getSelf().hasCallbackQuery()) {
            EditMessageText editMessage = new EditMessageText();
            editMessage.setChatId(chatId);
            editMessage.setMessageId(tgUpdate.getMessageId());
            editMessage.setText(currentStep.getText());
            editMessage.setParseMode(ParseMode.HTML);

            if (currentStep.getReplyKeyboard() != null && currentStep.getReplyKeyboard() instanceof InlineKeyboardMarkup)
                editMessage.setReplyMarkup((InlineKeyboardMarkup) currentStep.getReplyKeyboard());

            tgBot.execute(editMessage);
        } else {
            SendMessage sendMessage = new SendMessage();
            sendMessage.setChatId(chatId);
            sendMessage.setText(currentStep.getText());
            sendMessage.setParseMode(ParseMode.HTML);

            if (currentStep.getReplyKeyboard() != null) sendMessage.setReplyMarkup(currentStep.getReplyKeyboard());

            messagesId.add(tgBot.execute(sendMessage).getMessageId());
        }
    }

    @SneakyThrows
    public void removeMessages(TelegramLongPollingBot bot) {
        for (int messageId : messagesId)
            bot.execute(MessageUtil.delete(chatId, messageId));
    }
}

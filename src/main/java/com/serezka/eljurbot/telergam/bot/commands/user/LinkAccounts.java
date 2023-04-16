package com.serezka.eljurbot.telergam.bot.commands.user;

import com.serezka.eljurbot.Application;
import com.serezka.eljurbot.Lang;
import com.serezka.eljurbot.api.school.EljurApi;
import com.serezka.eljurbot.db.model.AuthorizationToken;
import com.serezka.eljurbot.db.model.User;
import com.serezka.eljurbot.db.services.AuthorizationTokenService;
import com.serezka.eljurbot.db.services.MarkService;
import com.serezka.eljurbot.db.services.UserService;
import com.serezka.eljurbot.telergam.bot.TelegramBot;
import com.serezka.eljurbot.telergam.bot.TelegramUpdate;
import com.serezka.eljurbot.telergam.bot.commands.Command;
import com.serezka.eljurbot.telergam.bot.session.type.step.Step;
import com.serezka.eljurbot.telergam.bot.session.type.step.StepGenerator;
import com.serezka.eljurbot.telergam.bot.util.KeyboardUtil;
import com.serezka.eljurbot.telergam.bot.util.MessageUtil;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.extern.java.Log;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.webapp.WebAppInfo;

import java.util.List;

// link tg account to eljur
@Service
@Log
@FieldDefaults(level = AccessLevel.PRIVATE)
@PropertySource("classpath:eljur.properties")
public class LinkAccounts extends Command {
    final UserService userService;
    final MarkService markService;
    final AuthorizationTokenService authorizationTokenService;

    public LinkAccounts(UserService userService,
                        AuthorizationTokenService authorizationTokenService,
                        MarkService markService) {

        super(Lang.LINK_ACCOUNTS_USE, Lang.LINK_ACCOUNTS_HELP, false);

        this.userService = userService;
        this.authorizationTokenService = authorizationTokenService;
        this.markService = markService;

        setStepGenerators(List.of(new StepGenerator(new Step(
                KeyboardUtil.getResizableReplyKeyboardMarkup(List.of(
                        new KeyboardUtil.ReplyButtonData("Авторизоваться", new WebAppInfo("https://depich.site/")),
                        new KeyboardUtil.ReplyButtonData(Lang.cancel)), 1),
                "Пройдите авторизацию с помощью <b>кнопки меню</b>:"))));
    }

    @Override
    public void execute(TelegramBot tgBot, TelegramUpdate tgUpdate, List<String> data) {
        String chatId = tgUpdate.getChatId();

        // check if user not use authorization via site
        if (tgUpdate.getSelf().hasMessage() && tgUpdate.getSelf().getMessage().getWebAppData() == null) {
            tgBot.sendMessage(chatId, Lang.LINK_ACCOUNTS_USE_SITE);
            return;
        }

        try {

            // try split data
            String[] authData = data.get(0).split(";");

            if (authData.length < 3) {
                tgBot.sendMessage(chatId, Lang.LINK_ACCOUNTS_ENTER_ALL_FIELDS);
                return;
            }

            String selectedVendor = null;
            String inputUsername = null;
            String inputPassword = null;
            String inputNikasoftUrl = null;

            // fill data
            if (!authData[0].equals("")) selectedVendor = authData[0];
            if (!authData[1].equals("")) inputUsername = authData[1];
            if (!authData[2].equals("")) inputPassword = authData[2];
            if (authData.length == 4 && !authData[3].equals("")) inputNikasoftUrl = authData[3];

            // get authorization token from data
            EljurApi eljurApi = EljurApi.getInstance();
            EljurApi.Authorization.TokenInfo tokenInfo = eljurApi.requestAuthorizationToken(inputUsername, inputPassword, selectedVendor);

            // check token
            if (tokenInfo == null || tokenInfo.getErrorText() != null) {
                tgBot.sendMessage(chatId, String.format(Lang.LINK_ACCOUNTS_ELJUR_RESPONSE_ERROR, tokenInfo != null ? tokenInfo.getErrorText() : "ошибка в логине/пароле/ID школы"));
                tgBot.execute(MessageUtil.sendSticker(chatId, Lang.STICKER_MASUNYA_FEAR));
                return;
            }

            // get user information from eljur
            EljurApi.UserInfo.Result userInfo = eljurApi.requestUserInfo(tokenInfo.getToken(), selectedVendor);
            User updatedUser = new User(chatId, inputUsername, inputPassword, selectedVendor, userInfo.getEljurId(),
                    userInfo.getRelations().getStudents().get(userInfo.getEljurId()).getSchoolClass());

            // set nikasoft url
            updatedUser.setNikasoftUrl(inputNikasoftUrl);

            // add user to database or replace
            if (userService.userExistsByChatId(chatId)) updatedUser.setId(userService.findUserByChatId(chatId).getId());
            boolean successSave = userService.saveUser(updatedUser);

            // check if user didn't added to database
            User userInDatabase = userService.findUserByChatId(chatId);
            if (userInDatabase == null) {
                log.warning("user didn't added to database! " + updatedUser);
                tgBot.execute(MessageUtil.sendMessage(chatId, Lang.LINK_ACCOUNTS_DATABASE_ERROR));
                return;
            }

            // remove old authorization token and create new
            // if user changes account
            authorizationTokenService.removeAllAuthorizationTokensByUserId(userInDatabase.getId());
            authorizationTokenService.saveAuthorizationToken(
                    new AuthorizationToken(
                            userInDatabase.getId(),
                            tokenInfo.getToken(),
                            Application.AUTHORIZATION_TOKEN_DATE_FORMAT.parse(tokenInfo.getExpires())
                    ));

            // send success message
            tgBot.execute(MessageUtil.sendMessage(chatId,
                   String.format(Lang.LINK_ACCOUNTS_SAVE,
                           successSave ? Lang.LINK_ACCOUNTS_SUCCESSFUL_SAVE : Lang.LINK_ACCOUNTS_UNSUCCESSFUL_SAVE,
                           userInDatabase.getEljurVendor(), userInDatabase.getEljurUsername(),
                           String.format("%1$"+userInDatabase.getEljurPassword().length()+ "s", "*").replace(' ', '*'),
                           userInfo.getRelations().getStudents().get(userInfo.getEljurId()).getSchoolClass(),
                           userInfo.getEljurId(), userInDatabase.getNikasoftUrl())));
            tgBot.execute(MessageUtil.sendSticker(chatId, Lang.STICKER_MASUNYA_LIKE));

            // delete old marks
            if (successSave) markService.deleteAllByUserId(userInDatabase.getId());
        } catch (Exception e) {
            tgBot.sendMessage(chatId, Lang.LINK_ACCOUNTS_AUTH_ERROR);
            log.warning(e.getMessage());
        }
    }
}

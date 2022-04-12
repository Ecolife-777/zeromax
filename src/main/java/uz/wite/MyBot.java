package uz.wite;

import lombok.SneakyThrows;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.lang.Math.toIntExact;

public class MyBot extends TelegramLongPollingBot {
    Map<String, Integer> linkStatus = new HashMap<>();
    Map<String, Link> tempLink = new HashMap<>();
    Map<String, Integer> tempAdmin1 = new HashMap<>();
    Map<String, Integer> tempAdmin2 = new HashMap<>();
    Map<String, String> lanStatus = new HashMap<>();
    Map<String, User> userStatus = new HashMap<>();
    Map<String, Ads> adStatus = new HashMap<>();
    Map<String, Integer> deleteLink = new HashMap<>();
    Map<String, Integer> deleteAds = new HashMap<>();

    @Override
    public String getBotUsername() {
        return "https://t.me/zero_maxbot";
    }

    @Override
    public String getBotToken() {
        return "5111674831:AAFR3_9IDHpdzeTmiFTBd_5_Y_yDgy7bJ6Q";
    }

    @SneakyThrows
    @Override
    public void onUpdateReceived(Update update) {
        DatabaseConnection connection = new DatabaseConnection();
        if (update.hasMessage()) {
            String chatId = update.getMessage().getChatId().toString();
            SendMessage sendMessage = new SendMessage();
            sendMessage.setChatId(chatId);
            if (update.getMessage().hasText()) {
                if (update.getMessage().getText().equals("/start")) {
                    sendMessage.setText("Assalomu alaykum botimizga hush kelibsiz");
                    sendMessage.setReplyMarkup(mainMenu(chatId));
                } else if (update.getMessage().getText().equals("Menu")) {
                    sendMessage.setText("Tilni tanlang\nВыберите язык\nChoose language");
                    sendMessage.setReplyMarkup(lanMenu());
                } else if (update.getMessage().getText().equals("Admin Menu")) {
                    sendMessage.setText("Welcome admin");
                    sendMessage.setReplyMarkup(adminMenu());
                }
                else if (linkStatus.size() != 0 && linkStatus.get(chatId) == 1) {
                    Link link = new Link();
                    link.setUzbName(update.getMessage().getText());
                    tempLink.put(chatId, link);
                    sendMessage.setText("havola ruscha nomini kiriting");
                    linkStatus.put(chatId, 0);
                }
                else if (tempLink.size() != 0 && tempLink.get(chatId).getUzbName() != null && tempLink.get(chatId).getRusName() == null) {
                    Link link = tempLink.get(chatId);
                    link.setRusName(update.getMessage().getText());
                    tempLink.put(chatId, link);
                    sendMessage.setText("havola ingilizcha nomini kiriting");
                }
                else if (tempLink.size() != 0 && tempLink.get(chatId).getRusName() != null && tempLink.get(chatId).getEngName() == null) {
                    Link link = tempLink.get(chatId);
                    link.setEngName(update.getMessage().getText());
                    tempLink.put(chatId, link);
                    sendMessage.setText("havola linkini kiriting");
                }
                else if (tempAdmin1.size() != 0 && tempAdmin1.get(chatId) == 1) {
                    tempAdmin1.put(chatId, 0);
                    try {
                        connection.saveAdmin(update.getMessage().getText());
                        sendMessage.setText("yangi admin qoshildi");
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                }
                else if (tempAdmin2.size() != 0 && tempAdmin2.get(chatId) == 1) {
                    tempAdmin2.put(chatId, 0);
                    try {
                        connection.deleteAdmin(update.getMessage().getText());
                        sendMessage.setText("admin ochirildi");
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                }
                else if (tempLink.size() != 0 && tempLink.get(chatId).getEngName() != null) {
                    Link link = tempLink.get(chatId);
                    link.setLink(update.getMessage().getText());
                    tempLink.put(chatId, null);
                    try {
                        connection.saveLink(link);
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                    sendMessage.setText("havola qoshildi");
                    sendMessage.setReplyMarkup(adminMenu());
                }
                else if (userStatus.size() != 0 && userStatus.get(chatId).getScreenId() != null && userStatus.get(chatId).getAdName() == null){
                    User user = userStatus.get(chatId);
                    user.setAdName(update.getMessage().getText());
                    userStatus.put(chatId, user);
                    Ads ads = new Ads();
                    ads.setName(update.getMessage().getText());
                    adStatus.put(chatId, ads);
                    if (lanStatus.get(chatId).equals("UZB")){
                        sendMessage.setText("Mahsulotingiz haqida malumot kiriting");
                    }
                    else if (lanStatus.get(chatId).equals("RUS")){
                        sendMessage.setText("Введите информацию о вашем продукте");
                    }
                    else {
                        sendMessage.setText("Enter information about your product");
                    }
                } else if (userStatus.size() != 0 && userStatus.get(chatId).getAdName() != null && userStatus.get(chatId).getDescription() == null){
                    User user = userStatus.get(chatId);
                    user.setDescription(update.getMessage().getText());
                    userStatus.put(chatId, user);
                    Ads ads = adStatus.get(chatId);
                    ads.setDescription(update.getMessage().getText());
                    adStatus.put(chatId, ads);
                    if (lanStatus.get(chatId).equals("UZB")){
                        sendMessage.setText("Mahsulotingizni bitta rasmini jonating");
                    }
                    else if (lanStatus.get(chatId).equals("RUS")){
                        sendMessage.setText("Отправьте одну фотографию вашего продукта");
                    }
                    else {
                        sendMessage.setText("Send a single picture of your product");
                    }
                }
                else {
                    sendMessage.setText("Hello");
                }
            }
            else if (update.getMessage().hasContact()){
                User user = new User();
                user.setUsername(update.getMessage().getContact().getFirstName());
                user.setPhoneNumber(update.getMessage().getContact().getPhoneNumber());
                userStatus.put(chatId, user);

                if (lanStatus.get(chatId).equals("UZB")){
                    sendMessage.setText("reklama narxi 50 000 som va uni quyidagi kartaga otkazishingizni soarymiz\n" +
                            "va chekini bizga jonatishingizni soraymiz\n" +
                            "1234 5678 1234 5678\n" +
                            "Palonchi Pistonchiyev");
                }
                else if (lanStatus.get(chatId).equals("RUS")){
                    sendMessage.setText("Стоимость рекламы 50 000 сомов и просим перевести на карту\n" +
                            "и просим прислать нам чек\n" +
                            "1234 5678 1234 5678\n" +
                            "Палончи Пистончиев");
                }
                else {
                    sendMessage.setText("The cost of advertising is 50,000 soms and we ask you to transfer it to the card\n" +
                            "and we ask you to send us the check\n" +
                            "1234 5678 1234 5678\n" +
                            "Palonchi Pistonchiyev");
                }
                sendMessage.setReplyMarkup(mainMenu(chatId));
            }
            else if (update.getMessage().hasPhoto()){
                if (userStatus.size() != 0 && userStatus.get(chatId).getPhoneNumber() != null && userStatus.get(chatId).getScreenId() == null){
                    User user = userStatus.get(chatId);
                    user.setScreenId(update.getMessage().getPhoto().get(0).getFileId());
                    userStatus.put(chatId, user);
                    if (lanStatus.get(chatId).equals("UZB")){
                        sendMessage.setText("Mahsulotingiz nomini kiriting");
                    }
                    else if (lanStatus.get(chatId).equals("RUS")){
                        sendMessage.setText("Введите название вашего продукта");
                    }
                    else {
                        sendMessage.setText("Enter the name of your product");
                    }
                }
                else if (userStatus.size() != 0
                        && userStatus.get(chatId).getDescription() != null
                        && userStatus.get(chatId).getFileId() == null){
                    User user = userStatus.get(chatId);
                    user.setFileId(update.getMessage().getPhoto().get(0).getFileId());
                    userStatus.put(chatId, user);
                    connection.saveUser(user);
                    Ads ads = adStatus.get(chatId);
                    ads.setFileId(update.getMessage().getPhoto().get(0).getFileId());
                    connection.saveAds(ads);
                    userStatus.put(chatId, new User());
                    adStatus.put(chatId, new Ads());
                    if (lanStatus.get(chatId).equals("UZB")){
                        sendMessage.setText("Reklamangiz adminga jonatildi");
                    }
                    else if (lanStatus.get(chatId).equals("RUS")){
                        sendMessage.setText("Ваше объявление отправлено администратору");
                    }
                    else {
                        sendMessage.setText("Your ad has been sent to the admin");
                    }
                }
            }
            try {
                execute(sendMessage);
            } catch (TelegramApiException e) {
                e.printStackTrace();
            }
        } else if (update.hasCallbackQuery()) {
            String call_data = update.getCallbackQuery().getData();
            long message_id = update.getCallbackQuery().getMessage().getMessageId();
            long chat_id = update.getCallbackQuery().getMessage().getChatId();
            EditMessageText new_message = new EditMessageText();
            new_message.setChatId(String.valueOf(chat_id));
            new_message.setMessageId(toIntExact(message_id));
            if (call_data.equals("UZB") || call_data.equals("RUS") || call_data.equals("ENG")) {
                lanStatus.put(String.valueOf(chat_id), call_data);
                if (call_data.equals("UZB")){
                    new_message.setText("Menuni tanlang");
                    try {
                        new_message.setReplyMarkup(mainMenuUzb(String.valueOf(chat_id)));
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                } else if (call_data.equals("RUS")){
                    new_message.setText("Выберите меню");
                    new_message.setReplyMarkup(mainMenuRus(String.valueOf(chat_id)));
                }
                else {
                    new_message.setText("Choose menu");
                    new_message.setReplyMarkup(mainMenuEng(String.valueOf(chat_id)));
                }
            }
            else if (call_data.equals("Bot haqida") || call_data.equals("О боте") || call_data.equals("About the bot")) {
                if (lanStatus.get(String.valueOf(chat_id)).equals("UZB")){
                    new_message.setText("Assalomu alaykum barchaga. Siz bu bot orqali uzingizga kerakli barcha narsalarni topishingiz mumkin." +
                            "Bizning maqsadimiz sizlarga qulaylik yaratish\n" +
                            "Admin bilan bog'lanish\n" +
                            "@XumoyunXaydarov");                }
                else if (lanStatus.get(String.valueOf(chat_id)).equals("RUS")){
                    new_message.setText("Всем привет. Через этого бота вы сможете найти все, что вам нужно." +
                            "Наша цель - чтобы вам было удобно\n" +
                            "Свяжитесь с администратором\n" +
                            "@XumoyunXaydarov");
                }
                else {
                    new_message.setText("Hello everyone. You can find everything you need through this bot. " +
                            "Our goal is to make it convenient for you \n" +
                            "Contact the admin\n" +
                            "@XumoyunXaydarov");
                }
            }
            else if (call_data.equals("Barcha havolalarni korish") || call_data.equals("Посмотреть все ссылки") || call_data.equals("View all links")) {
                if (lanStatus.get(String.valueOf(chat_id)).equals("UZB")){
                    new_message.setText("Istaganingizni bosing");
                }
                else if (lanStatus.get(String.valueOf(chat_id)).equals("RUS")){
                    new_message.setText("Щелкните тот, который вы хотите");
                }
                else {
                    new_message.setText("Click the one you want");
                }

                try {
                    new_message.setReplyMarkup(links(1, lanStatus.get(String.valueOf(chat_id))));
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            else if (call_data.equals("Admin panel")) {
                new_message.setText("welcome admin");
                new_message.setReplyMarkup(adminMenu());
            }
            else if (call_data.equals("Admin qoshish")) {
                new_message.setText("admin chat idisini kiriting");
                tempAdmin1.put(String.valueOf(chat_id), 1);
            }
            else if (call_data.equals("Admin ochirish")) {
                new_message.setText("admin chat idisini kiriting");
                tempAdmin2.put(String.valueOf(chat_id), 1);
            }
            else if (call_data.equals("Havola qoshish")) {
                new_message.setText("havola uzbekcha nomini kriting");
                linkStatus.put(String.valueOf(chat_id), 1);
            }
            else if (call_data.equals("Reklamalar")) {
                new_message.setText("hamma reklamalar");
                try {
                    ads(chat_id);
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            else if (call_data.equals("Reklama ochirish")) {
                deleteAds.put(String.valueOf(chat_id), 1);
                new_message.setText("reklama tanlang");
                try {
                    new_message.setReplyMarkup(getAds());
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            else if (call_data.equals("Havola ochirish")) {
                deleteLink.put(String.valueOf(chat_id), 1);
                new_message.setText("havolani tanlang");
                try {
                    new_message.setReplyMarkup(links(0, lanStatus.get(String.valueOf(chat_id))));
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            else if (call_data.equals("Botga reklama berish")
                    || call_data.equals("Рекламировать бота")
                    || call_data.equals("Advertise the bot")){
                SendMessage sendMessage = new SendMessage();
                if (lanStatus.get(String.valueOf(chat_id)).equals("UZB")){
                    sendMessage.setText("Kontaktni ulashish");
                    new_message.setText("Assalomu alaykum hurmatli mijoz siz o'z maxsulotlaringiz reklamalarini" +
                            " shu yerdan junatishingiz mumkin va biz sizni reklamangizni 2 kun ichida kanalimizga yuklaymiz! ✅\n\n" +
                            "\uD83D\uDDE3 Asosiy talablar bilan tanishib chiqing\n" +
                            "Biz sizdan qonunga zid bo'lmagan maxsulotlarni reklama qilishingizni qat'iy so'raymiz, " +
                            "aks holda bu o'zingizga qarshi ishlashi mumkin. ‼️\n\n" +
                            "Bizni talablarimizga rozi bo'lsangiz boshlashingiz mumkin.");
                }
                else if (lanStatus.get(String.valueOf(chat_id)).equals("RUS")){
                    sendMessage.setText("Поделиться контактом");
                    new_message.setText("Здравствуйте уважаемый клиент, вы можете рекламировать свою продукцию" +
                            "Вы можете отправить его сюда, и мы загрузим вашу рекламу на наш канал в течение 2 дней! ✅\n\n" +
                            "\uD83D\uDDE3Ознакомиться с основными требованиями\n" +
                            "Мы настоятельно рекомендуем вам рекламировать товары, не нарушающие закон," +
                            "иначе это может сработать против вас. ‼ \n\n" +
                            "Вы можете начать нас, если согласны с нашими требованиями.");
                }
                else {
                    sendMessage.setText("Share contact");
                    new_message.setText("Hello dear customer, you can advertise your products " +
                            "You can send it here and we will upload your ad to our channel within 2 days! ✅\n\n" +
                            "\uD83D\uDDE3Get acquainted with the basic requirements\n" +
                            "We strongly urge you to advertise products that do not violate the law," +
                            "otherwise it can work against you. ‼️\n\n" +
                            "You can start us if you agree to our requirements.");
                }
                sendMessage.setChatId(String.valueOf(chat_id));
                sendMessage.setReplyMarkup(shareContact(chat_id));
                execute(sendMessage);
            }
            else {
                if (deleteLink.size() != 0 && deleteLink.get(String.valueOf(chat_id)) == 1){
                    connection.deleteLink(call_data);
                    new_message.setText("link ochirildi");
                    deleteLink.put(String.valueOf(chat_id), 0);
                }
                else if (deleteAds.size() != 0 && deleteAds.get(String.valueOf(chat_id)) == 1){
                    connection.deleteUser(call_data);
                    connection.deleteAd(call_data);
                    new_message.setText("reklama ochirildi");
                    deleteAds.put(String.valueOf(chat_id), 0);
                }
            }
            try {
                execute(new_message);
            } catch (TelegramApiException e) {
                e.printStackTrace();
            }
        }
    }

    public InlineKeyboardMarkup lanMenu() {
        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> inlineKeyboardButtons = new ArrayList<>();
        List<InlineKeyboardButton> keyboardButtons = new ArrayList<>();
        InlineKeyboardButton keyboardButton = new InlineKeyboardButton();
        keyboardButton.setText("UZBEK \uD83C\uDDFA\uD83C\uDDFF ");
        keyboardButton.setCallbackData("UZB");
        InlineKeyboardButton keyboardButton1 = new InlineKeyboardButton();
        keyboardButton1.setText("РУССКИЙ \uD83C\uDDF7\uD83C\uDDFA ");
        keyboardButton1.setCallbackData("RUS");
        InlineKeyboardButton keyboardButton2 = new InlineKeyboardButton();
        keyboardButton2.setText("ENGLAND \uD83C\uDFF4\uDB40\uDC67\uDB40\uDC62\uDB40\uDC65\uDB40\uDC6E\uDB40\uDC67\uDB40\uDC7F");
        keyboardButton2.setCallbackData("ENG");
        keyboardButtons.add(keyboardButton);
        keyboardButtons.add(keyboardButton1);
        keyboardButtons.add(keyboardButton2);
        inlineKeyboardButtons.add(keyboardButtons);

        inlineKeyboardMarkup.setKeyboard(inlineKeyboardButtons);
        return inlineKeyboardMarkup;
    }

    public InlineKeyboardMarkup mainMenuUzb(String chatId) throws SQLException {
        DatabaseConnection connection = new DatabaseConnection();
        List<String> allAdmins = connection.getAllAdmins();
        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> inlineKeyboardButtons = new ArrayList<>();
        List<InlineKeyboardButton> keyboardButtons = new ArrayList<>();
        List<InlineKeyboardButton> keyboardButtons1 = new ArrayList<>();
        InlineKeyboardButton keyboardButton = new InlineKeyboardButton();
        keyboardButton.setText("Bot haqida");
        keyboardButton.setCallbackData("Bot haqida");
        InlineKeyboardButton keyboardButton1 = new InlineKeyboardButton();
        keyboardButton1.setText("Botga reklama berish");
        keyboardButton1.setCallbackData("Botga reklama berish");
        InlineKeyboardButton keyboardButton2 = new InlineKeyboardButton();
        keyboardButton2.setText("Barcha havolalarni korish");
        keyboardButton2.setCallbackData("Barcha havolalarni korish");
        InlineKeyboardButton keyboardButton3 = new InlineKeyboardButton();
        keyboardButton3.setText("Admin panel");
        keyboardButton3.setCallbackData("Admin panel");
        keyboardButtons.add(keyboardButton);
        keyboardButtons.add(keyboardButton1);
        keyboardButtons1.add(keyboardButton2);
        if (allAdmins.contains(chatId)) {
            keyboardButtons1.add(keyboardButton3);
        }
        inlineKeyboardButtons.add(keyboardButtons);
        inlineKeyboardButtons.add(keyboardButtons1);

        inlineKeyboardMarkup.setKeyboard(inlineKeyboardButtons);
        return inlineKeyboardMarkup;
    }

    public InlineKeyboardMarkup mainMenuRus(String chatId) throws SQLException {
        DatabaseConnection connection = new DatabaseConnection();
        List<String> allAdmins = connection.getAllAdmins();
        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> inlineKeyboardButtons = new ArrayList<>();
        List<InlineKeyboardButton> keyboardButtons = new ArrayList<>();
        List<InlineKeyboardButton> keyboardButtons1 = new ArrayList<>();
        InlineKeyboardButton keyboardButton = new InlineKeyboardButton();
        keyboardButton.setText("О боте");
        keyboardButton.setCallbackData("О боте");
        InlineKeyboardButton keyboardButton1 = new InlineKeyboardButton();
        keyboardButton1.setText("Рекламировать бота");
        keyboardButton1.setCallbackData("Рекламировать бота");
        InlineKeyboardButton keyboardButton2 = new InlineKeyboardButton();
        keyboardButton2.setText("Посмотреть все ссылки");
        keyboardButton2.setCallbackData("Посмотреть все ссылки");
        InlineKeyboardButton keyboardButton3 = new InlineKeyboardButton();
        keyboardButton3.setText("Admin panel");
        keyboardButton3.setCallbackData("Admin panel");
        keyboardButtons.add(keyboardButton);
        keyboardButtons.add(keyboardButton1);
        keyboardButtons1.add(keyboardButton2);
        if (allAdmins.contains(chatId)) {
            keyboardButtons1.add(keyboardButton3);
        }
        inlineKeyboardButtons.add(keyboardButtons);
        inlineKeyboardButtons.add(keyboardButtons1);

        inlineKeyboardMarkup.setKeyboard(inlineKeyboardButtons);
        return inlineKeyboardMarkup;
    }

    public InlineKeyboardMarkup mainMenuEng(String chatId) throws SQLException {
        DatabaseConnection connection = new DatabaseConnection();
        List<String> allAdmins = connection.getAllAdmins();
        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> inlineKeyboardButtons = new ArrayList<>();
        List<InlineKeyboardButton> keyboardButtons = new ArrayList<>();
        List<InlineKeyboardButton> keyboardButtons1 = new ArrayList<>();
        InlineKeyboardButton keyboardButton = new InlineKeyboardButton();
        keyboardButton.setText("About the bot");
        keyboardButton.setCallbackData("About the bot");
        InlineKeyboardButton keyboardButton1 = new InlineKeyboardButton();
        keyboardButton1.setText("Advertise the bot");
        keyboardButton1.setCallbackData("Advertise the bot");
        InlineKeyboardButton keyboardButton2 = new InlineKeyboardButton();
        keyboardButton2.setText("View all links");
        keyboardButton2.setCallbackData("View all links");
        InlineKeyboardButton keyboardButton3 = new InlineKeyboardButton();
        keyboardButton3.setText("Admin panel");
        keyboardButton3.setCallbackData("Admin panel");
        keyboardButtons.add(keyboardButton);
        keyboardButtons.add(keyboardButton1);
        keyboardButtons1.add(keyboardButton2);
        if (allAdmins.contains(chatId)) {
            keyboardButtons1.add(keyboardButton3);
        }
        inlineKeyboardButtons.add(keyboardButtons);
        inlineKeyboardButtons.add(keyboardButtons1);

        inlineKeyboardMarkup.setKeyboard(inlineKeyboardButtons);
        return inlineKeyboardMarkup;
    }

    public InlineKeyboardMarkup adminMenu() {
        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> inlineKeyboardButtons = new ArrayList<>();
        List<InlineKeyboardButton> keyboardButtons = new ArrayList<>();
        List<InlineKeyboardButton> keyboardButtons1 = new ArrayList<>();
        List<InlineKeyboardButton> keyboardButtons2 = new ArrayList<>();
        InlineKeyboardButton keyboardButton = new InlineKeyboardButton();
        keyboardButton.setText("Reklamalar");
        keyboardButton.setCallbackData("Reklamalar");
        InlineKeyboardButton keyboardButton1 = new InlineKeyboardButton();
        keyboardButton1.setText("Reklama ochirish");
        keyboardButton1.setCallbackData("Reklama ochirish");
        InlineKeyboardButton keyboardButton2 = new InlineKeyboardButton();
        keyboardButton2.setText("Havola qoshish");
        keyboardButton2.setCallbackData("Havola qoshish");
        InlineKeyboardButton keyboardButton3 = new InlineKeyboardButton();
        keyboardButton3.setText("Havola ochirish");
        keyboardButton3.setCallbackData("Havola ochirish");
        InlineKeyboardButton keyboardButton4 = new InlineKeyboardButton();
        keyboardButton4.setText("Admin qoshish");
        keyboardButton4.setCallbackData("Admin qoshish");
        InlineKeyboardButton keyboardButton5 = new InlineKeyboardButton();
        keyboardButton5.setText("Admin ochirish");
        keyboardButton5.setCallbackData("Admin ochirish");
        keyboardButtons.add(keyboardButton);
        keyboardButtons.add(keyboardButton1);
        keyboardButtons1.add(keyboardButton2);
        keyboardButtons1.add(keyboardButton3);
        keyboardButtons2.add(keyboardButton4);
        keyboardButtons2.add(keyboardButton5);
        inlineKeyboardButtons.add(keyboardButtons);
        inlineKeyboardButtons.add(keyboardButtons1);
        inlineKeyboardButtons.add(keyboardButtons2);

        inlineKeyboardMarkup.setKeyboard(inlineKeyboardButtons);
        return inlineKeyboardMarkup;
    }

    public InlineKeyboardMarkup links(Integer num, String name) throws SQLException {
        DatabaseConnection connection = new DatabaseConnection();
        List<Link> allLinks = connection.getAllLinks();
        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> inlineKeyboardButtons = new ArrayList<>();
        List<InlineKeyboardButton> keyboardButtons = new ArrayList<>();
        if (num == 1) {
            if (name.equals("UZB")) {
                for (int i = 0; i < allLinks.size(); i++) {
                    InlineKeyboardButton keyboardButton = new InlineKeyboardButton();
                    keyboardButton.setText(allLinks.get(i).getUzbName());
                    keyboardButton.setUrl(allLinks.get(i).getLink());
                    keyboardButton.setCallbackData("1");
                    keyboardButtons.add(keyboardButton);
                    if ((i + 1) % 3 == 0) {
                        inlineKeyboardButtons.add(keyboardButtons);
                        keyboardButtons = new ArrayList<>();
                    }
                }
            }
            else if (name.equals("RUS")){
                for (int i = 0; i < allLinks.size(); i++) {
                    InlineKeyboardButton keyboardButton = new InlineKeyboardButton();
                    keyboardButton.setText(allLinks.get(i).getRusName());
                    keyboardButton.setUrl(allLinks.get(i).getLink());
                    keyboardButton.setCallbackData("1");
                    keyboardButtons.add(keyboardButton);
                    if ((i + 1) % 3 == 0) {
                        inlineKeyboardButtons.add(keyboardButtons);
                        keyboardButtons = new ArrayList<>();
                    }
                }
            }
            else {
                for (int i = 0; i < allLinks.size(); i++) {
                    InlineKeyboardButton keyboardButton = new InlineKeyboardButton();
                    keyboardButton.setText(allLinks.get(i).getEngName());
                    keyboardButton.setUrl(allLinks.get(i).getLink());
                    keyboardButton.setCallbackData("1");
                    keyboardButtons.add(keyboardButton);
                    if ((i + 1) % 3 == 0) {
                        inlineKeyboardButtons.add(keyboardButtons);
                        keyboardButtons = new ArrayList<>();
                    }
                }
            }
        } else {
            if (name.equals("UZB")) {
                for (int i = 0; i < allLinks.size(); i++) {
                    InlineKeyboardButton keyboardButton = new InlineKeyboardButton();
                    keyboardButton.setText(allLinks.get(i).getUzbName());
                    keyboardButton.setCallbackData(allLinks.get(i).getUzbName());
                    keyboardButtons.add(keyboardButton);
                    if ((i + 1) % 3 == 0) {
                        inlineKeyboardButtons.add(keyboardButtons);
                        keyboardButtons = new ArrayList<>();
                    }
                }
            }
            else if (name.equals("RUS")){
                for (int i = 0; i < allLinks.size(); i++) {
                    InlineKeyboardButton keyboardButton = new InlineKeyboardButton();
                    keyboardButton.setText(allLinks.get(i).getRusName());
                    keyboardButton.setCallbackData(allLinks.get(i).getUzbName());
                    keyboardButtons.add(keyboardButton);
                    if ((i + 1) % 3 == 0) {
                        inlineKeyboardButtons.add(keyboardButtons);
                        keyboardButtons = new ArrayList<>();
                    }
                }
            }
            else {
                for (int i = 0; i < allLinks.size(); i++) {
                    InlineKeyboardButton keyboardButton = new InlineKeyboardButton();
                    keyboardButton.setText(allLinks.get(i).getEngName());
                    keyboardButton.setCallbackData(allLinks.get(i).getUzbName());
                    keyboardButtons.add(keyboardButton);
                    if ((i + 1) % 3 == 0) {
                        inlineKeyboardButtons.add(keyboardButtons);
                        keyboardButtons = new ArrayList<>();
                    }
                }
            }
        }
        inlineKeyboardButtons.add(keyboardButtons);
        inlineKeyboardMarkup.setKeyboard(inlineKeyboardButtons);
        return inlineKeyboardMarkup;
    }

    public void ads(Long chatId) throws SQLException {
        DatabaseConnection connection = new DatabaseConnection();
        List<User> user = connection.getAllUser();
        for (int i = 0; i < user.size(); i++) {
            SendPhoto message = new SendPhoto();
            message.setChatId(String.valueOf(chatId));
            message.setCaption(user.get(i).getUsername() + "\n" + user.get(i).getPhoneNumber());
            message.setPhoto(new InputFile(user.get(i).getScreenId()));
            Ads ad = connection.getAdByFileId(user.get(i).getFileId());
            SendPhoto message1 = new SendPhoto();
            message1.setChatId(String.valueOf(chatId));
            message1.setCaption(ad.getName() + "\n" + user.get(i).getDescription());
            message1.setPhoto(new InputFile(user.get(i).getFileId()));
            try {
                execute(message);
                execute(message1);
            } catch (TelegramApiException e) {
                e.printStackTrace();
            }
        }
    }

    public InlineKeyboardMarkup getAds() throws SQLException {
        DatabaseConnection connection = new DatabaseConnection();
        List<Ads> ads = connection.getAllAds();
        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> inlineKeyboardButtons = new ArrayList<>();
        List<InlineKeyboardButton> keyboardButtons = new ArrayList<>();
        for (int i = 0; i < ads.size(); i++) {
            InlineKeyboardButton keyboardButton = new InlineKeyboardButton();
            keyboardButton.setText(ads.get(i).getName());
            keyboardButton.setCallbackData(ads.get(i).getName());
            keyboardButtons.add(keyboardButton);
            if ((i + 1) % 2 == 0) {
                inlineKeyboardButtons.add(keyboardButtons);
                keyboardButtons = new ArrayList<>();
            }
        }
        inlineKeyboardButtons.add(keyboardButtons);
        inlineKeyboardMarkup.setKeyboard(inlineKeyboardButtons);
        return inlineKeyboardMarkup;
    }

    public ReplyKeyboardMarkup shareContact(Long chat_id){
        ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup();
        replyKeyboardMarkup.setSelective(true);
        replyKeyboardMarkup.setResizeKeyboard(true);
        replyKeyboardMarkup.setOneTimeKeyboard(true);

        // new list
        List<KeyboardRow> keyboard = new ArrayList<>();

        // first keyboard line
        KeyboardRow keyboardFirstRow = new KeyboardRow();
        KeyboardButton keyboardButton = new KeyboardButton();
        if (lanStatus.get(String.valueOf(chat_id)).equals("UZB")){
            keyboardButton.setText("Raqamingizni junatish >");
        }
        else if (lanStatus.get(String.valueOf(chat_id)).equals("RUS")){
            keyboardButton.setText("Отправьте свой номер >");
        }
        else {
            keyboardButton.setText("Share your number >");
        }
        keyboardButton.setRequestContact(true);
        keyboardFirstRow.add(keyboardButton);

        // add array to list
        keyboard.add(keyboardFirstRow);

        // add list to our keyboard
        replyKeyboardMarkup.setKeyboard(keyboard);
        return replyKeyboardMarkup;
    }

    public ReplyKeyboardMarkup mainMenu(String chatId) throws SQLException {
        DatabaseConnection connection = new DatabaseConnection();
        List<String> allAdmins = connection.getAllAdmins();
        ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup();
        replyKeyboardMarkup.setSelective(true);
        replyKeyboardMarkup.setResizeKeyboard(true);
        replyKeyboardMarkup.setOneTimeKeyboard(false);

        // new list
        List<KeyboardRow> keyboard = new ArrayList<>();

        // first keyboard line
        KeyboardRow keyboardFirstRow = new KeyboardRow();
        KeyboardButton keyboardButton = new KeyboardButton();
        keyboardButton.setText("Menu");
        KeyboardButton keyboardButton1 = new KeyboardButton();
        keyboardButton1.setText("Admin Menu");
        keyboardFirstRow.add(keyboardButton);
        if (allAdmins.contains(chatId)){
            keyboardFirstRow.add(keyboardButton1);
        }

        // add array to list
        keyboard.add(keyboardFirstRow);

        // add list to our keyboard
        replyKeyboardMarkup.setKeyboard(keyboard);
        return replyKeyboardMarkup;
    }
}

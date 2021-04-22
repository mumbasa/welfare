package com.welfare.sys;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendDocument;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.PhotoSize;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import com.welfare.data.Staff;
import com.welfare.repository.LoanRepository;
import com.welfare.repository.ShareRepository;
import com.welfare.repository.StaffRepository;


public class Alerter extends TelegramLongPollingBot {
	@Value("${bot.username}")
	private String username;

	@Value("${bot.token}")
	private String token;

	@Autowired
	StaffRepository staffRepository;
	@Autowired
	ShareRepository shareRepository;

	@Autowired
	LoanRepository loanRepository;

	SendMessage sendMessage;

	@Override
	public void onUpdateReceived(Update update) {
		// TODO Auto-generated method stub
		long chatId = update.getMessage().getChatId();

		if (update.hasMessage()) {
			if (update.getMessage().hasText()) {

				String message = update.getMessage().getText();
				if (message.contains("/register")) {
					Long id = Long.parseLong(message.split("/register")[1].trim());
					Staff staff = staffRepository.getStaff(id);
					if (staff != null) {
						staffRepository.telegramRegister(id, chatId);
						sendMessage = new SendMessage(chatId,
								"Thanks for registering\n" + staff.getName() + "\nPlease upload your picture");
						try {
							if (sendMessage == null) {

							} else {
								execute(sendMessage);
							}
						} catch (TelegramApiException | NullPointerException e) { // TODO Auto-generated catch block
							e.printStackTrace();

						}
					} else {
						sendMessage = new SendMessage(chatId,
								"Sorry your details is not in our systeam \nPlease provide the correct controller number");
						try {
							if (sendMessage == null) {

							} else {
								execute(sendMessage);
							}
						} catch (TelegramApiException | NullPointerException e) { // TODO Auto-generated catch block
							e.printStackTrace();

						}
					}
				}

				else if (message.toLowerCase().trim().contains("summary")) {
					Staff staff = staffRepository.getStaffByTelegram(chatId);

					List<Double> summ = staffRepository.staffStatus(staff.getStaffId());
					if (staff != null) {
						sendMessage = new SendMessage(chatId,
								"You have paid GHC" + summ.get(1) + " of <b>GHC" + summ.get(0)
										+ "</b> of the loans taken.\nYour welfare share fund is <b>GHC" + summ.get(2)
										+ "</b>");
						sendMessage.enableHtml(true);
						try {
							if (sendMessage == null) {

							} else {
								execute(sendMessage);
							}
						} catch (TelegramApiException | NullPointerException e) { // TODO Auto-generated catch block
							e.printStackTrace();

						}
					}
				}

				else if (message.contains("/help")) {

					sendMessage = new SendMessage(chatId,
							"Type <b>shares statement</b> for your shares payments statement"
									+ "\nType <b>loans statement</b> for your loan payments statement\n"
									+ "Type <b>summary</b> for your summary statemetns\nThank you");
					sendMessage.enableHtml(true);
					try {
						if (sendMessage == null) {

						} else {
							execute(sendMessage);
						}
					} catch (TelegramApiException | NullPointerException e) { // TODO Auto-generated catch block
						e.printStackTrace();

					}
				}

				else if (message.toLowerCase().contains("shares statement")) {
					Staff staff = staffRepository.getStaffByTelegram(chatId);

					if (staff != null) {
						SendDocument docu = new SendDocument();
						docu.setChatId(chatId);
						docu.setDocument(shareRepository.getShareStatment(staff.getStaffId()));
						docu.setCaption("Your shares payments Statement");
						try {
							execute(docu);
						} catch (TelegramApiException e) { // TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				}

				else if (message.toLowerCase().contains("loans statement")) {
					Staff staff = staffRepository.getStaffByTelegram(chatId);

					if (staff != null) {
						SendDocument docu = new SendDocument();
						docu.setChatId(chatId); //
						docu.setDocument(loanRepository.getLoanstatment(staff.getStaffId()));
						docu.setCaption("Your loan payments Statement");
						try {
							execute(docu);
						} catch (TelegramApiException e) { // TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				}

				else if (message.toLowerCase().contains("my loans")) {
					Staff staff = staffRepository.getStaffByTelegram(chatId);

					if (staff != null) {
						SendDocument docu = new SendDocument();
						docu.setChatId(chatId); //
						docu.setDocument(loanRepository.getLoansFile(staff.getStaffId()));
						docu.setCaption("Your approved loan");
						try {
							execute(docu);
						} catch (TelegramApiException e) { // TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				}

			}
		
		
		}
		if (update.getMessage().hasPhoto()) {
			PhotoSize s = update.getMessage().getPhoto().get(0);
			staffRepository.download(s.getFileId(), chatId);
			staffRepository.uploadPicture(chatId + ".jpg", chatId);
			sendMessage = new SendMessage(chatId,
					"Thank you for uploading your picture.\nYou can now interact with me.\nFor all my available commands type \\help");
			try {
				if (sendMessage == null) {

				} else {
					execute(sendMessage);
				}
			} catch (TelegramApiException | NullPointerException e) { // TODO Auto-generated catch block
				e.printStackTrace();

			}
		}

		

	}

	@Override
	public String getBotUsername() {
		// TODO Auto-generated method stub
		return username;
	}

	@Override
	public String getBotToken() {
		// TODO Auto-generated method stub
		return token;
	}

}

package controllers;


import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.inject.Inject;

import jj.play.ns.nl.captcha.text.producer.FiveLetterFirstNameTextProducer;

import models.BankAccount;
import models.Operation;
import models.OperationPrevision;
import models.Tag;
import models.User;
import services.BankAccountService;
import services.TagService;
import services.UserService;
import util.Chart;

import common.AuthController;
import common.RenderJsonWithExclusion;

public class Banks extends AuthController {

	@Inject
	static BankAccountService bankAccountService;
	@Inject
	static UserService userService;
	@Inject
	static TagService tagService;

	public static void index() {
		User u = Application.getAuthUser();
		List<BankAccount> bankAccountList = BankAccount.find("byUser", u)
				.fetch();
		// List<BankAccount> bankAccountList = u.bankAccountList;

		renderArgs.put("tags", userService.getAllTags());
		renderArgs.put("bankAccountList", bankAccountList);

		render();
	}

	public static void showJSON(Long id) {
		BankAccount bankAccount = BankAccount.findById(id);
		List<Operation> operations = null;
		operations = Operation.find("bankAccount = ? order by date DESC",
				bankAccount).fetch(30);

		renderJSONWithExclude(operations);
	}

	public static void jsonAdd(Long id, Long amount, String name) {
		Operation operation = new Operation();
		operation.name = name;
		operation.amount = amount;
		operation.fictive = true;

		operation.date = new Date();

		operation.bankAccount = BankAccount.findById(id);
		operation.tags.add(tagService.getOrCreateByName("liquide"));

		bankAccountService.saveOperation(operation);

		renderText("ok");
		// flash.success("Operation %s created", name);
	}

	private static void renderJSONWithExclude(List<Operation> operations) {
		throw new RenderJsonWithExclusion(operations);
	}

    public static void jsonBank(Long id, String beginDate, String endDate) {
        // prepare date
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
        Calendar cal = Calendar.getInstance();

        if (endDate == null) {

            if (beginDate == null) {

                int month = cal.get(Calendar.MONTH);
                int year = cal.get(Calendar.YEAR);
                cal.set(year, month, 1);

                beginDate = dateFormat.format(cal.getTime());
            } else {
                try {
                    cal.setTime(dateFormat.parse(beginDate));
                } catch (ParseException e) {
                    flash.error("Your interval seems to be corrupted");
                    //show(id, null, null);
                }
            }
            cal.add(Calendar.MONTH, 1);
            endDate = dateFormat.format(cal.getTime());

            //show(id, beginDate, endDate);
        }

        Date origDate = null;

        try {
            origDate = dateFormat.parse(beginDate);
            cal.setTime(origDate);
        } catch (ParseException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }

        renderArgs.put("year", cal.get(Calendar.YEAR));
        renderArgs.put("month", cal.get(Calendar.MONTH) + 1);

        renderArgs.put("beginDate", beginDate);
        renderArgs.put("endDate", endDate);

        // retrieve bank account
        BankAccount bankAccount = BankAccount.findById(id);

        List<Operation> operations = null;
        try {
            operations = Operation
                    .find("bankAccount = ? and (date >= ? and date < ? )order by date ASC",
                            bankAccount, dateFormat.parse(beginDate),
                            dateFormat.parse(endDate)).fetch();
        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        renderArgs.put("bankAccount", bankAccount);
        renderArgs.put("operations", operations);

        // fetch all budget

        Map<Tag, Double> budgets = new TreeMap<Tag, Double>();
        Map<Tag, Double> estimations = new TreeMap<Tag, Double>();
        for (Tag t : userService.getAllTags()) {
            Double l = 0d;
            Double est = 0d;
            try {
                l = Double.valueOf(bankAccountService.calculateBudgetForTag(
                        bankAccount, dateFormat.parse(beginDate),
                        dateFormat.parse(endDate), t).toString());
                est = bankAccountService.calculateEstimation(bankAccount, t)
                        .doubleValue();
            } catch (NumberFormatException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (ParseException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

            budgets.put(t, l);
            estimations.put(t, est);
            // budgets.put(t, 3l);
        }
        renderArgs.put("budgets", budgets);
        renderArgs.put("estimations", estimations);

        // ////////////////////////////////////////////////////////
        // feth all operationPrevisions

        Collection<OperationPrevision> operationPrevisionsList = null;
        try {
            operationPrevisionsList = bankAccountService
                    .getAllOperationPrevisions(dateFormat.parse(beginDate),
                            dateFormat.parse(endDate));
        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        renderArgs.put("operationPrevisions", operationPrevisionsList);

        renderArgs.put("chartImg", Chart.generateBudgetChartImg(budgets));

        Double somme = Double.valueOf(bankAccountService.getAmountAt(
                bankAccount, origDate).toString());
        renderArgs.put("initialSomme", somme);

        for (Operation op : operations) {
            // if it's not a fictive operation
            if (op.fictive == false) {
                somme += op.amount;
            }
        }

        Double previsionSomme = somme;
        // System.out.println("somme:"+ somme);
        for (OperationPrevision op : operationPrevisionsList) {
            Double tagBudget = 0d;
            try {
                tagBudget = Double.valueOf(bankAccountService
                        .calculateBudgetForTag(bankAccount,
                                dateFormat.parse(beginDate),
                                dateFormat.parse(endDate), op.tag).toString());
            } catch (NumberFormatException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (ParseException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            if (Math.abs(tagBudget) < Math.abs(op.amount)) {
                previsionSomme += (op.amount - tagBudget);
                // System.out.println(op.amount);
                // System.out.println("-:"+ (op.amount + tagBudget));
            }
        }

        renderArgs.put("previsionSomme", previsionSomme);

        renderArgs.put("somme", somme);
        renderArgs.put("tags", userService.getAllTags());

        new RenderJsonWithExclusion(previsionSomme);
    }


    public static void show(Long id, String beginDate, String endDate) {
		// prepare date
		SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
		Calendar cal = Calendar.getInstance();

		if (endDate == null) {

			if (beginDate == null) {

				int month = cal.get(Calendar.MONTH);
				int year = cal.get(Calendar.YEAR);
				cal.set(year, month, 1);

				beginDate = dateFormat.format(cal.getTime());
			} else {
				try {
					cal.setTime(dateFormat.parse(beginDate));
				} catch (ParseException e) {
					flash.error("Your interval seems to be corrupted");
					show(id, null, null);
				}
			}
			cal.add(Calendar.MONTH, 1);
			endDate = dateFormat.format(cal.getTime());

			show(id, beginDate, endDate);
		}

		Date origDate = null;

		try {
			origDate = dateFormat.parse(beginDate);
			cal.setTime(origDate);
		} catch (ParseException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		renderArgs.put("year", cal.get(Calendar.YEAR));
		renderArgs.put("month", cal.get(Calendar.MONTH) + 1);

		renderArgs.put("beginDate", beginDate);
		renderArgs.put("endDate", endDate);

		// retrieve bank account
		BankAccount bankAccount = BankAccount.findById(id);

		List<Operation> operations = null;
		try {
			operations = Operation
					.find("bankAccount = ? and (date >= ? and date < ? )order by date ASC",
							bankAccount, dateFormat.parse(beginDate),
							dateFormat.parse(endDate)).fetch();
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		renderArgs.put("bankAccount", bankAccount);
		renderArgs.put("operations", operations);

		// fetch all budget

		Map<Tag, Double> budgets = new TreeMap<Tag, Double>();
		Map<Tag, Double> estimations = new TreeMap<Tag, Double>();
		for (Tag t : userService.getAllTags()) {
			Double l = 0d;
			Double est = 0d;
			try {
				l = Double.valueOf(bankAccountService.calculateBudgetForTag(
						bankAccount, dateFormat.parse(beginDate),
						dateFormat.parse(endDate), t).toString());
				est = bankAccountService.calculateEstimation(bankAccount, t)
						.doubleValue();
			} catch (NumberFormatException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			budgets.put(t, l);
			estimations.put(t, est);
			// budgets.put(t, 3l);
		}
		renderArgs.put("budgets", budgets);
		renderArgs.put("estimations", estimations);

		// ////////////////////////////////////////////////////////
		// feth all operationPrevisions

		Collection<OperationPrevision> operationPrevisionsList = null;
		try {
			operationPrevisionsList = bankAccountService
					.getAllOperationPrevisions(dateFormat.parse(beginDate),
							dateFormat.parse(endDate));
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		renderArgs.put("operationPrevisions", operationPrevisionsList);

		renderArgs.put("chartImg", Chart.generateBudgetChartImg(budgets));

		Double somme = Double.valueOf(bankAccountService.getAmountAt(
				bankAccount, origDate).toString());
		renderArgs.put("initialSomme", somme);

		for (Operation op : operations) {
			// if it's not a fictive operation
			if (op.fictive == false) {
				somme += op.amount;
			}
		}

		Double previsionSomme = somme;
		// System.out.println("somme:"+ somme);
		for (OperationPrevision op : operationPrevisionsList) {
			Double tagBudget = 0d;
			try {
				tagBudget = Double.valueOf(bankAccountService
						.calculateBudgetForTag(bankAccount,
								dateFormat.parse(beginDate),
								dateFormat.parse(endDate), op.tag).toString());
			} catch (NumberFormatException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			if (Math.abs(tagBudget) < Math.abs(op.amount)) {
				previsionSomme += (op.amount - tagBudget);
				// System.out.println(op.amount);
				// System.out.println("-:"+ (op.amount + tagBudget));
			}
		}

		renderArgs.put("previsionSomme", previsionSomme);

		renderArgs.put("somme", somme);
		renderArgs.put("tags", userService.getAllTags());

		render();
	}

	public static void showXLS(Long id, String beginDate, String endDate) {
		// prepare date
		SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
		Calendar cal = Calendar.getInstance();

		if (endDate == null) {

			if (beginDate == null) {

				int month = cal.get(Calendar.MONTH);
				int year = cal.get(Calendar.YEAR);
				cal.set(year, month, 1);

				beginDate = dateFormat.format(cal.getTime());
			} else {
				try {
					cal.setTime(dateFormat.parse(beginDate));
				} catch (ParseException e) {
					flash.error("Your interval seems to be corrupted");
					show(id, null, null);
				}
			}
			cal.add(Calendar.MONTH, 1);
			endDate = dateFormat.format(cal.getTime());

			show(id, beginDate, endDate);
		}

		Date origDate = null;

		try {
			origDate = dateFormat.parse(beginDate);
			cal.setTime(origDate);
		} catch (ParseException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		renderArgs.put("year", cal.get(Calendar.YEAR));
		renderArgs.put("month", cal.get(Calendar.MONTH) + 1);

		renderArgs.put("beginDate", beginDate);
		renderArgs.put("endDate", endDate);

		// retrieve bank account
		BankAccount bankAccount = BankAccount.findById(id);

		List<Operation> operations = null;
		try {
			operations = Operation
					.find("bankAccount = ? and (date >= ? and date < ? )order by date ASC",
							bankAccount, dateFormat.parse(beginDate),
							dateFormat.parse(endDate)).fetch();
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		renderArgs.put("bankAccount", bankAccount);
		renderArgs.put("operations", operations);

		// fetch all budget

		Map<Tag, Double> budgets = new TreeMap<Tag, Double>();
		Map<Tag, Double> estimations = new TreeMap<Tag, Double>();
		for (Tag t : userService.getAllTags()) {
			Double l = 0d;
			Double est = 0d;
			try {
				l = Double.valueOf(bankAccountService.calculateBudgetForTag(
						bankAccount, dateFormat.parse(beginDate),
						dateFormat.parse(endDate), t).toString());
				est = bankAccountService.calculateEstimation(bankAccount, t)
						.doubleValue();
			} catch (NumberFormatException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			budgets.put(t, l);
			estimations.put(t, est);
			// budgets.put(t, 3l);
		}
		renderArgs.put("budgets", budgets);
		renderArgs.put("estimations", estimations);

		// ////////////////////////////////////////////////////////
		// feth all operationPrevisions

		Collection<OperationPrevision> operationPrevisionsList = null;
		try {
			operationPrevisionsList = bankAccountService
					.getAllOperationPrevisions(dateFormat.parse(beginDate),
							dateFormat.parse(endDate));
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		renderArgs.put("operationPrevisions", operationPrevisionsList);

		renderArgs.put("chartImg", Chart.generateBudgetChartImg(budgets));

		Double somme = Double.valueOf(bankAccountService.getAmountAt(
				bankAccount, origDate).toString());
		renderArgs.put("initialSomme", somme);

		for (Operation op : operations) {
			// if it's not a fictive operation
			if (op.fictive == false) {
				somme += op.amount;
			}
		}

		Double previsionSomme = somme;
		// System.out.println("somme:"+ somme);
		for (OperationPrevision op : operationPrevisionsList) {
			Double tagBudget = 0d;
			try {
				tagBudget = Double.valueOf(bankAccountService
						.calculateBudgetForTag(bankAccount,
								dateFormat.parse(beginDate),
								dateFormat.parse(endDate), op.tag).toString());
			} catch (NumberFormatException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			if (Math.abs(tagBudget) < Math.abs(op.amount)) {
				previsionSomme += (op.amount - tagBudget);
				// System.out.println(op.amount);
				// System.out.println("-:"+ (op.amount + tagBudget));
			}
		}

		renderArgs.put("previsionSomme", previsionSomme);

		renderArgs.put("somme", somme);
		renderArgs.put("tags", userService.getAllTags());

		renderArgs.put("fileName", "export.xls");

		List<HashMap<String, String>> formattedOperations = new LinkedList<HashMap<String, String>>();

		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");

		for (Operation operation : operations) {
			HashMap<String, String> entry = new HashMap<String, String>();

			StringBuilder tags = new StringBuilder("");
			for (Tag t : operation.tags) {
				if (t.visible) {
					tags.append(t.name);
					tags.append(", ");
				}
			}

			String tagStr = tags.toString();

			entry.put("name", operation.name.trim());
			entry.put("date", sdf.format(operation.date));
			entry.put("amount", operation.amount + "");
			entry.put("tags", tagStr);
			// entry.put("tags");

			formattedOperations.add(entry);
		}

		//renderArgs.put("formattedOperations", formattedOperations);
        render();
		//renderExcel();
	}

	/**
	 * 
	 * @param name
	 */
	public static void delete(Long id) {
		bankAccountService.delete(id);
		// flash.success("Account %s deleted", bankAccount.name);
	}

	public static void save(List<Long> selected) {
		String res = "";
		for (Long id : selected) {
			res += id + "\n";
		}

		renderText(res);

	}

	/**
	 * 
	 * @param name
	 */
	public static void deleteOperation(Long id) {
		flash.success("Operation %s deleted",
				bankAccountService.getOperationById(id).name);
		bankAccountService.deleteOperation(id);
	}

	public static void addOperation(String name, String date, Double amount,
			Long bankId) {

		Operation operation = new Operation();
		operation.name = name;
		operation.amount = amount;

		if (params.get("fictive") != null) {
			operation.fictive = true;
		} else {
			operation.fictive = false;
		}

		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy");

		try {
			operation.date = simpleDateFormat.parse(date);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		operation.bankAccount = BankAccount.findById(bankId);

		bankAccountService.saveOperation(operation);

		flash.success("Operation %s created", name);

		show(bankId, null, null);
	}

	public static void add(String name) {
		User u = Application.getAuthUser();
		BankAccount bankAccount = new BankAccount();
		bankAccount.name = name;
		bankAccount.user = u;

		bankAccountService.save(bankAccount);

		flash.success("Account %s created", name);

		index();
	}

	public static void importBNPCSV(Long bankId, File importFile) {

		// find bank
		BankAccount bAccount = bankAccountService.getById(bankId);

		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yy");

		BufferedReader bufRdr = null;
		try {
			bufRdr = new BufferedReader(new FileReader(importFile));
		} catch (FileNotFoundException e) {
			flash.error("Can't open file");
			show(bankId, null, null);
			return;
		}

		String line = null;
		try {
			bufRdr.readLine();
			while ((line = bufRdr.readLine()) != null) {
				String[] res = line.split("\t");

				// create operation
				Operation op = new Operation();
				res[2] = res[2].replace(",", ".");
				op.amount = Double.valueOf(res[2]);
				op.name = res[1];

				try {
					op.date = (Date) simpleDateFormat.parse(res[0]);
				} catch (ParseException e) {
					// TODO Auto-generated catch block
					flash.error("Can't parse date format %s", res[0]);
					show(bankId, null, null);
				}

				op.bankAccount = bAccount;
				if (!bankAccountService.operationExists(op)) {

					// bankAccountService.saveOperation(op);

					/*
					 * if (op.name.contains("CARTE")) {
					 * op.tags.add(tagService.getOrCreateByName("CB")); }
					 */

					op.name = op.name.replace("([ ])*DU ([ 0-9])*", "");
					op.name = op.name.replace("FACTURE CARTE", "");
					op.tags.add(tagService.getOrCreateByName("needCheck"));

					if (op.name.contains("DAB")) {
						op.tags.add(tagService.getOrCreateByName("DAB"));

					}

					if (op.name.contains("PRELEVEMENT")) {
						Tag t = tagService.getOrCreateByName("PREL");
						op.name = op.name.replace("PRELEVEMENT ", "");
						op.tags.add(t);
						// remove check for PREL
						op.tags.remove(tagService
								.getOrCreateByName("needCheck"));
					}

					if (op.name.contains("ECHEANCE")) {
						Tag t = tagService.getOrCreateByName("PREL");

						// remove check for ECHEANCE
						op.tags.remove(tagService
								.getOrCreateByName("needCheck"));
						op.tags.add(t);
					}

					// course
					for (String s : Arrays.asList("LECLER", "CARREFOUR",
							"INTERMARCHE", "CORA", "NORMA")) {
						if (op.name.contains(s)) {
							op.tags.add(tagService.getOrCreateByName("COURSE"));
						}
					}
					bankAccountService.saveOperation(op);

				}

			}
		} catch (IOException e) {
			flash.error("Can't read the file");
			show(bankId, null, null);
			return;
		}

		try {
			bufRdr.close();
		} catch (IOException e) {
			flash.error("Can't close the file");
			show(bankId, null, null);
			return;
		}

		flash.success("Import successfull");
		show(bankId, null, null);
	}

	public static void batch(List<Long> selected, String jsAction,
			String jsParam) {
		if (selected == null) {
			return;
		}

		for (Long l : selected) {
			System.out.println("retrieve " + l);
			Operation op = bankAccountService.getOperationById(l);

			if (jsAction.equals("addTag")) {
				op.tags.add(tagService.getOrCreateByName(jsParam));
				bankAccountService.saveOperation(op);
			} else if (jsAction.equals("delTag")) {
				op.tags.remove(tagService.getOrCreateByName(jsParam));
				bankAccountService.saveOperation(op);
			} else if (jsAction.equals("delOp")) {
				bankAccountService.deleteOperation(l);
			}
		}
	}
}

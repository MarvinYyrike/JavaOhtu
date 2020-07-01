package com.javaohtu.bankcontroller.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.web.bind.annotation.*;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@RequestMapping("bank")
@RestController()

public class BankController {

    @Autowired
    private NamedParameterJdbcTemplate namedParameterJdbcTemplate;
    //selle kaudu saab suhelda andmebaasiga SQLiga

    @GetMapping("getbalance/{iBan}")
    public List<AccountNumber> getBalance(@PathVariable("iBan") String iBan) { //kasutaja lisatud ibani ei tohi otse SQLi panna
        String sql = "SELECT * FROM accountnumber WHERE iban = :iban";
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("iban",iBan); //sqli sees olevad muutujad
        List<AccountNumber> result = namedParameterJdbcTemplate.query(sql, paramMap, new ObjectRowMapper());
        return result;

                // tuleb teha objekt, kuhu andmebaasist salvestab ühe rea ja siis saab seda välja kutsuda
    }

    @PostMapping("createaccount/{firstname}/{lastname}/{idcustomers}/{iban}/{idaccount}/{balance}")
    public void createAccount (@PathVariable ("firstname") String firstName, @PathVariable("lastname") String lastName, @PathVariable("idcustomers") int idCustomer , @PathVariable("iban") String iBan, @PathVariable("idaccount") int idAccount , @PathVariable("balance") double balance) {
        String sql1 = "INSERT INTO accountnumber (id, balance, customer_id, iban) VALUES (:idaccountnumber, :balance, :customer_id, :iban)";
        String sql2 = "INSERT INTO customers (id, firstname, lastname) VALUES (:customer_id, :firstname, :lastname)";
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("firstname", firstName);
        paramMap.put("lastname", lastName);
        paramMap.put("customer_id", idCustomer);
        paramMap.put("iban", iBan);
        paramMap.put("idaccountnumber", idAccount);
        paramMap.put("balance", balance);
        namedParameterJdbcTemplate.update(sql1,paramMap);
        namedParameterJdbcTemplate.update(sql2,paramMap);
    }

    @PutMapping("deposit/{iban}/{sum}")
    public void depositMoney(@PathVariable("iban") String iBan, @PathVariable ("sum") double depositAmount){
        String sql = "UPDATE accountnumber set balance = balance + :deposit where iban = :iban";
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("iban", iBan);
        paramMap.put("deposit", depositAmount);
        namedParameterJdbcTemplate.update(sql,paramMap);
    }

    @PutMapping("withdraw/{iban}/{sum}")
    public void withdrawMoney(@PathVariable("iban") String iBan, @PathVariable ("sum") double withdrawAmount){
        //selectiga vajasaada balance väärtus ja seda võrrelda withdrawga

        String sql = "UPDATE accountnumber set balance = balance - :deposit where (iban = :iban and balance >= withdraw)";
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("iban", iBan);
        paramMap.put("withdraw", withdrawAmount);
        namedParameterJdbcTemplate.update(sql,paramMap);
    }


    private class ObjectRowMapper implements RowMapper<AccountNumber> {
        @Override
        public AccountNumber mapRow(ResultSet resultSet, int i) throws SQLException {
            AccountNumber accountNumber = new AccountNumber();
            accountNumber.setIban(resultSet.getString("iban"));
            accountNumber.setBalance(resultSet.getDouble("balance"));
            accountNumber.setCustomer_id(resultSet.getInt("customer_id"));
            accountNumber.setId(resultSet.getInt("id"));
            return accountNumber;
        }
    }

    //TODO
    //create account
    //get balance
    // deposit
    // withdraw
    //transfer

    

}

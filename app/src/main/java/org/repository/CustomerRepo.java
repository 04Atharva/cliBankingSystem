
package org.repository;

import org.DAO.Customer;
import org.DAO.Transaction;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CustomerRepo {

    private final Map<String, org.DAO.Customer> customerById = new HashMap<>();

    public List<Customer> findAll() {
        return new ArrayList<>(customerById.values());
    }
    public void save(Customer c) {
        customerById.put(c.getId(), c);
    }
}


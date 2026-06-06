package service;

import exception.EntityNotFoundException;
import model.Location;
import model.Merchant;
import model.MerchantCategory;
import util.IdGenerator;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class MerchantService {
    private final Map<String, Merchant> merchants = new HashMap<>();

    public Merchant registerMerchant(String name, MerchantCategory category, Location location) {
        String id = IdGenerator.nextMerchantId();
        Merchant merchant = new Merchant(id, name, category, location);
        merchants.put(id, merchant);
        return merchant;
    }

    public Merchant findById(String id) {
        Merchant merchant = merchants.get(id);
        if (merchant == null) {
            throw new EntityNotFoundException("Merchant not found: " + id);
        }
        return merchant;
    }

    public Collection<Merchant> findAll() {
        return Collections.unmodifiableCollection(merchants.values());
    }
}
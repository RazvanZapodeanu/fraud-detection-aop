package service;

import exception.EntityNotFoundException;
import model.Analyst;
import util.IdGenerator;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class AnalystService {
    private final Map<String, Analyst> analysts = new HashMap<>();

    public Analyst createAnalyst(String name, String email) {
        String id = IdGenerator.nextAnalystId();
        Analyst analyst = new Analyst(id, name, email);
        analysts.put(id, analyst);
        return analyst;
    }

    public Analyst findById(String id) {
        Analyst analyst = analysts.get(id);
        if (analyst == null) {
            throw new EntityNotFoundException("Analyst not found: " + id);
        }
        return analyst;
    }

    public Collection<Analyst> findAll() {
        return Collections.unmodifiableCollection(analysts.values());
    }

    public void recordResolution(String analystId) {
        findById(analystId).incrementResolved();
    }
}
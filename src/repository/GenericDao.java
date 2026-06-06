package repository;

import java.sql.Connection;
import java.util.List;

public abstract class GenericDao<T> {

    protected final Connection connection;

    protected GenericDao() {
        this.connection = DatabaseConnection.getInstance().getConnection();
    }

    public abstract void insert(T entity);
    public abstract T findById(String id);
    public abstract List<T> findAll();
    public abstract void update(T entity);
    public abstract void delete(String id);
}
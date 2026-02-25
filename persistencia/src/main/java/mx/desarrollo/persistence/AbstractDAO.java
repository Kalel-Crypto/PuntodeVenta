package mx.desarrollo.persistence;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.NoResultException;
import jakarta.persistence.StoredProcedureQuery;

import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;

public abstract class AbstractDAO<T> {

    private final Class<T> entityClass;

    protected AbstractDAO(Class<T> entityClass) {
        this.entityClass = entityClass;
    }

    protected abstract EntityManager getEntityManager();

      /* ========================
       Operaciones CRUD básicas
       ======================== */

    // Save or persist a new entity
    public void save(T entity) {
        executeInsideTransaction(em -> em.persist(entity));
    }

    // Update an existing entity
    public void update(T entity) {
        executeInsideTransaction(em -> em.merge(entity));
    }

    // Delete an entity
    public void delete(T entity) {
        executeInsideTransaction(em -> em.remove(em.contains(entity) ? entity : em.merge(entity)));
    }

    // Find by ID
    public Optional<T> find(Object id) {
        return Optional.ofNullable(getEntityManager().find(entityClass, id));
    }

    public Optional<T> findFresh(Object id) {
        return Optional.ofNullable(execute(em -> {
            T entity = em.find(entityClass, id);
            if (entity != null) {
                em.refresh(entity);
            }
            return entity;
        }));
    }

    // Find all
    public List<T> findAll() {
        return execute(em ->
                em.createQuery("SELECT e FROM " + entityClass.getSimpleName() + " e", entityClass)
                        .getResultList()
        );
    }

    public T saveOrUpdate(T entity) {
        return execute(em -> {
            T merged = em.merge(entity);
            em.flush();
            return merged;
        });
    }

    /* ========================
       Consultas personalizadas
       ======================== */


    public int executeCountQuery(String query) {
        return execute(em -> Math.toIntExact(em.createQuery(query, Long.class).getSingleResult()));
    }


    public List<T> findFromWhere(String de, String campo, String criterio) {
        String jpql = "SELECT DISTINCT a FROM " + entityClass.getCanonicalName()
                + " a JOIN a." + de + " b WHERE b." + campo + " = :value";

        return execute(em ->
                em.createQuery(jpql, entityClass)
                        .setParameter("value", criterio)
                        .getResultList()
        );
    }

    public T findByOneParameterUnique(String value, String field) {
        String jpql = "SELECT e FROM " + entityClass.getSimpleName()
                + " e WHERE e." + field + " = :value";
        return execute(em -> {
            try {
                return em.createQuery(jpql, entityClass)
                        .setParameter("value", value)
                        .getSingleResult();
            } catch (NoResultException ex) {
                return null;
            }
        });
    }

    // Find by field value (generic single-field query)
    public List<T> findByField(String fieldName, Object value) {
        return findByOneParameter(value, fieldName);
    }

    public List<T> findByOneParameter(Object value, String field) {
        String jpql = "SELECT e FROM " + entityClass.getSimpleName()
                + " e WHERE e." + field + " = :value";
        return execute(em ->
                em.createQuery(jpql, entityClass)
                        .setParameter("value", value)
                        .getResultList()
        );
    }

    /* ========================
       SQL / procedimientos
       ======================== */

    /**
     * Ejecuta un procedimiento almacenado que no requiere parámetros.
     *
     * @param procedureName nombre del procedimiento
     * @return lista de resultados mapeados
     */
    public List<T> executeProcedure(String procedureName) {
        return execute(em -> {
            StoredProcedureQuery query = em.createStoredProcedureQuery(procedureName, entityClass);
            return query.getResultList();
        });
    }



    public List<T> executeNativeQuery(String sql) {
        return execute(em ->
                em.createNativeQuery(sql, entityClass)
                        .getResultList()
        );
    }

      /* ========================
       Helpers internos
       ======================== */


    // Utility to run in transaction
    private void executeInsideTransaction(Consumer<EntityManager> action) {
        execute(em -> {
            action.accept(em);
            return null;
        });
    }

    // Optional: for custom return values
    protected <R> R execute(Function<EntityManager, R> function) {
        EntityManager em = getEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            R result = function.apply(em);
            tx.commit();
            return result;
        } catch (RuntimeException e) {
            if (tx.isActive()) tx.rollback();
            throw e;
        }
    }


}

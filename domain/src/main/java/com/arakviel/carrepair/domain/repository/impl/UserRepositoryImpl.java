package com.arakviel.carrepair.domain.repository.impl;

import com.arakviel.carrepair.domain.exception.DomainAddException;
import com.arakviel.carrepair.domain.exception.DomainNotFoundException;
import com.arakviel.carrepair.domain.impl.User;
import com.arakviel.carrepair.domain.mapper.MapperFactory;
import com.arakviel.carrepair.domain.mapper.UserMapper;
import com.arakviel.carrepair.domain.repository.UserRepository;
import com.arakviel.carrepair.domain.validator.user.UserValidatorChain;
import com.arakviel.carrepair.persistence.DaoFactory;
import com.arakviel.carrepair.persistence.dao.UserDao;
import com.arakviel.carrepair.persistence.entity.impl.UserEntity;
import com.arakviel.carrepair.persistence.filter.impl.UserFilterDto;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import org.mindrot.jbcrypt.BCrypt;

public final class UserRepositoryImpl implements UserRepository {

    private final UserDao userDao;
    private final UserMapper userMapper;
    private final UserValidatorChain userValidatorChain;

    public UserRepositoryImpl() {
        userDao = DaoFactory.getInstance().getUserDao();
        userMapper = MapperFactory.getInstance().getUserMapper();
        userValidatorChain = UserValidatorChain.getInstance();
    }

    /**
     * Get the entire collection of entities by filtering in RDMS.
     *
     * @return collection of Domains
     */
    @Override
    public List<User> getAllWhere(String email, String login) {
        var filter = new UserFilterDto(email, login);
        List<UserEntity> userEntities = userDao.findAll(filter);
        return userEntities.stream().map(userMapper::toDomain).toList();
    }

    /**
     * Get a domain object by identifier.
     *
     * @param id primary key identifier
     * @return D domain object
     */
    @Override
    public User get(UUID id) {
        UserEntity userEntity = userDao.findOneById(id)
                .orElseThrow(() -> new DomainNotFoundException("Не вдалось знайти користувачу по ідентифікатору"));
        return userMapper.toDomain(userEntity);
    }

    /**
     * Get the entire collection of entities.
     *
     * @return collection of Entities
     */
    @Override
    public List<User> getAll() {
        List<UserEntity> userEntities = userDao.findAll();
        return userEntities.stream().map(userMapper::toDomain).toList();
    }

    /**
     * Save the entity to the database table.
     *
     * @param user persistent entity
     * @return entity, with identifier of the last added record from the database
     */
    @Override
    public User add(User user) {
        validate(user);

        user.setId(null);
        String hashedPassword = BCrypt.hashpw(user.getPassword(), BCrypt.gensalt());
        user.setPassword(hashedPassword);
        var userEntity = userMapper.toEntity(user);
        userEntity = userDao.save(userEntity);
        return userMapper.toDomain(userEntity);
    }

    /**
     * Update the domain to the database table.
     *
     * @param id ідентифікатор
     * @param user persistent entity
     */
    @Override
    public void set(UUID id, User user) {
        validate(user);
        user.setId(id);
        get(id);

        String hashedPassword = BCrypt.hashpw(user.getPassword(), BCrypt.gensalt());
        user.setPassword(hashedPassword);
        var userEntity = userMapper.toEntity(user);

        userDao.save(userEntity);
    }

    /**
     * Delete domain object from collection.
     *
     * @param id primary key identifier
     */
    @Override
    public void remove(UUID id) {
        get(id);
        userDao.remove(id);
    }

    @Override
    public Map<String, List<String>> getValidationMessages() {
        return userValidatorChain.getValidationMessages();
    }

    private void validate(User user) {
        if (!userValidatorChain.validate(user)) {
            throw new DomainAddException("Не вдалось додати користувачу із-за не валідних даних");
        } else if (!getAllWhere(user.getEmail(), null).isEmpty() && Objects.isNull(user.getId())) {
            List<String> email = getValidationMessages().get("email");
            email.add("Аналогічний email вже присутній в базі");
        } else if (!getAllWhere(null, user.getLogin()).isEmpty() && Objects.isNull(user.getId())) {
            List<String> email = getValidationMessages().get("login");
            email.add("Аналогічний login вже присутній в базі");
        }
    }
}

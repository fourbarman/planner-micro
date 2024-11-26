package ru.fourbarman.planner.micro.plannertodo.service;

import org.springframework.stereotype.Service;
import ru.fourbarman.planner.micro.plannerentity.entity.Category;
import ru.fourbarman.planner.micro.plannertodo.repository.CategoryRepository;

import javax.transaction.Transactional;
import java.util.List;


// всегда нужно создавать отдельный класс Service для доступа к данным, даже если кажется,
// что мало методов или это все можно реализовать сразу в контроллере
// Такой подход полезен для будущих доработок и правильной архитектуры (особенно, если работаете с транзакциями)
@Service

// все методы класса должны выполниться без ошибки, чтобы транзакция завершилась
// если в методе выполняются несолько SQL запросов и возникнет исключение - то все выполненные операции откатятся (Rollback)
@Transactional
public class CategoryService {

    // работает встроенный механизм DI из Spring, который при старте приложения подставит в эту переменную нужные класс-реализацию
    private final CategoryRepository repository; // сервис имеет право обращаться к репозиторию (БД)
    private final CategoryRepository categoryRepository;

    public CategoryService(CategoryRepository repository, CategoryRepository categoryRepository) {
        this.repository = repository;
        this.categoryRepository = categoryRepository;
    }

    //возвращет optional, поэтому get()
    public Category findById(Long id) {
        return repository.findById(id).get();
    }

    public List<Category> findAll(String userId) {
        return repository.findByUserIdOrderByTitleAsc(userId);
    }

    public Category add(Category category) {
        return categoryRepository.save(category);
    }

    //save создает новый или обновляет объект
    public void update(Category category) {
        categoryRepository.save(category);
    }

    public void deleteById(Long id) {
        repository.deleteById(id);
    }

    public List<Category> findByTitle(String title, String userId) {
        return repository.findByTitle(title, userId);
    }
}

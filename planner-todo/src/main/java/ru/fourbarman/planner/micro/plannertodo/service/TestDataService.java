package ru.fourbarman.planner.micro.plannertodo.service;

import org.springframework.stereotype.Service;
import ru.fourbarman.planner.micro.plannerentity.entity.Category;
import ru.fourbarman.planner.micro.plannerentity.entity.Priority;
import ru.fourbarman.planner.micro.plannerentity.entity.Task;

import java.util.Calendar;
import java.util.Date;

@Service
public class TestDataService {
    private final CategoryService categoryService;
    private final PriorityService priorityService;
    private final TaskService taskService;

    public TestDataService(CategoryService categoryService, PriorityService priorityService, TaskService taskService) {
        this.categoryService = categoryService;
        this.priorityService = priorityService;
        this.taskService = taskService;
    }

    public void initTestData(String userId) {
        Priority priority1 = new Priority();
        priority1.setColor("#fff");
        priority1.setTitle("Важный");
        priority1.setUserId(userId);

        Priority priority2 = new Priority();
        priority2.setColor("#ffe");
        priority2.setTitle("Неважный");
        priority2.setUserId(userId);

        priorityService.add(priority1);
        priorityService.add(priority2);

        Category category1 = new Category();
        category1.setTitle("Работа");
        category1.setUserId(userId);

        Category category2 = new Category();
        category2.setTitle("Семья");
        category2.setUserId(userId);

        categoryService.add(category1);
        categoryService.add(category2);

        //завтра
        Date tomorrow = new Date();
        Calendar c = Calendar.getInstance();
        c.setTime(tomorrow);
        c.add(Calendar.DATE, 1);
        tomorrow = c.getTime();

        //неделя
        Date oneWeek = new Date();
        Calendar c2 = Calendar.getInstance();
        c2.setTime(oneWeek);
        c2.add(Calendar.DATE, 7);
        oneWeek = c2.getTime();

        Task task1 = new Task();
        task1.setTitle("Покушать");
        task1.setCategory(category1);
        task1.setPriority(priority1);
        task1.setCompleted(true);
        task1.setTaskDate(tomorrow);
        task1.setUserId(userId);

        Task task2 = new Task();
        task2.setTitle("Поспать");
        task2.setCategory(category2);
        task2.setPriority(priority2);
        task2.setCompleted(false);
        task2.setTaskDate(oneWeek);
        task2.setUserId(userId);

        taskService.add(task1);
        taskService.add(task2);
    }
}

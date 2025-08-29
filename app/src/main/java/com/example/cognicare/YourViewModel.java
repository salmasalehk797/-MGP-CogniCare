package com.example.cognicare;

import android.app.Application;
import androidx.lifecycle.AndroidViewModel;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import androidx.lifecycle.LiveData;

public class YourViewModel extends AndroidViewModel {

    private UserDao yourEntityDao, yourDao;
    private LiveData<Boolean> isDatabaseEmpty;
    private LiveData<List<UserEntity>> allData;
    private YourRepository repository;
    private ExecutorService executorService = Executors.newSingleThreadExecutor();

    public YourViewModel(Application application) {
        super(application);
        AppDatabase database = AppDatabase.getInstance(application);
        yourEntityDao = database.UserDao();
        repository = new YourRepository(application);
        isDatabaseEmpty = repository.getIsDatabaseEmpty();
    }

    public UserEntity getAllData() {
        return yourEntityDao.getUser();
    }

    public boolean isDatabaseEmpty() {
        return allData.getValue() == null || allData.getValue().isEmpty();
    }

    public LiveData<Boolean> getIsDatabaseEmpty() {
        return isDatabaseEmpty;
    }

    public void saveFormData(FormData formData) {
        executorService.execute(() -> {
            UserEntity yourEntity = new UserEntity();
            yourEntity.setFullName(formData.getFullName());
            //yourEntity.setAge(formData.getAge());
            yourEntity.setNumSiblings(formData.getNumSiblings());
            yourEntity.setMotherName(formData.getMotherName());
            yourEntity.setFatherName(formData.getFatherName());

            yourEntityDao.insert(yourEntity);
        });
    }

    public void deleteSetupData() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                yourEntityDao.deleteSetupData();
            }
        }).start();
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        executorService.shutdown(); // Shutdown the executor when ViewModel is cleared
    }

    public static class EditDataActivity {
    }
}




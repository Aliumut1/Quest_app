package com.example.questapp.controllers;

import com.example.questapp.entities.User;
import com.example.questapp.exceptions.UserNotFoundException;
import com.example.questapp.repos.UserRepository;
import com.example.questapp.responses.UserResponse;
import com.example.questapp.services.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/users")
public class UserController {
    private UserService userService; //=bir şey diyerek atamadık tanımladığımız şeye
    public UserController(UserService userService){
        this.userService =userService; //Spring bootun getirdiği şeyi atıyormuş.
    }

    @GetMapping //Getirmek için
    public List<User> getAllUsers(){

        return userService.getAllUsers();
    }

    @PostMapping //Bir şeyler oluşturmak için. hem get hem post ayrı ayrı yazabiliriz.
    public User createUser(@RequestBody User newUser){

        return userService.saveOneUser(newUser);
    }
    //Birisi /usersa post isteği atmaya kalktığında


    @GetMapping("/{userId}")//bunu sayfanın en üstündeki şeye append ediyor ekliyor ucuna. /users/userId pathini biri çağırdığında bu
    //post mapping yapamayız. bizim databaseimize id vererek birinin kaydolmasını istemeyiz. id creationı biz handle ediyoruz.
    public UserResponse getOneUser(@PathVariable Long userId){
        //custom exception
        User user = userService.getOneUserById(userId);
        if(user == null){
            throw new UserNotFoundException();
        }

        return new UserResponse(user);
    }


    @PutMapping("/{userId}")
    public User updateOneUser(@PathVariable Long userId, @RequestBody User newUser){//requestbody ile updatelenecek userrı alıyoruz
        return userService.updateOneUser(userId, newUser); //sen service olarak bana arada ne yaparsan yap bana yeni userı dön diyorum.
    }

    @DeleteMapping("/{userId}")//tüm usrları bir anda sildirmek istemeyiz. o usere silinsin.
    public void deleteOneUser(@PathVariable Long userId){
        userService.deleteById(userId);//userRepositorydeki deletebyid metodu ile sileceğiz
    }


    @GetMapping("/activity/{userId}")
    public List<Object> getUserActivity(@PathVariable Long userId){
        return userService.getUserActivity(userId);
    }


    //Her türlü n

    @ExceptionHandler(UserNotFoundException.class) //bu tipteki exceptionları karşılayacak alttaki class
    @ResponseStatus(HttpStatus.NOT_FOUND)
    private void handleUserNotFound(){
        //burada istersek response body de dönebiliriz
    }

}

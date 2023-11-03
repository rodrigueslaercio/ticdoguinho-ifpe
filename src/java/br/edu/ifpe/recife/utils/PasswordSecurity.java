/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.edu.ifpe.recife.utils;

import org.mindrot.jbcrypt.BCrypt;

/**
 *
 * @author laerc
 */
public class PasswordSecurity {

    public static String encrypt(String password) {
        return BCrypt.hashpw(password, BCrypt.gensalt());
    }

    public static boolean decrypt(String candidate, String encrypted) {
        return BCrypt.checkpw(candidate, encrypted);
    }
}

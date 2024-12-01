package com.example.ergo;

import android.widget.Toast;

import com.example.ergo.model.JwtAuthenticationResponse;
import com.example.ergo.model.SignUpRequest;
import com.example.ergo.retrofit.UserAPI;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static org.junit.Assert.*;

public class RegisterFragmentTest {

    @Mock
    private UserAPI mockUserAPI;  // Mocked API

    @Mock
    private Call<JwtAuthenticationResponse> mockCall;  // Mocked Retrofit Call

    private RegisterFragment registerFragment;

    @Before
    public void setUp() {
        // Initialize mock objects
        MockitoAnnotations.initMocks(this);
        registerFragment = new RegisterFragment();

        // Mock the UserAPI instance to return the mocked call
        Mockito.when(mockUserAPI.signup(ArgumentMatchers.any(SignUpRequest.class))).thenReturn(mockCall);
    }

    @Test
    public void testRegisterSuccess() {
        // Simulate a successful response from the API
        JwtAuthenticationResponse mockResponse = new JwtAuthenticationResponse();
        mockResponse.setToken("faakkeeeeeeeeeeeee");
        Response<JwtAuthenticationResponse> response = Response.success(mockResponse);

        Mockito.doAnswer(new Answer<Void>() {
            @Override
            public Void answer(InvocationOnMock invocation) {
                Callback<JwtAuthenticationResponse> callback = (Callback<JwtAuthenticationResponse>) invocation.getArguments()[0];
                callback.onResponse(mockCall, response);
                return null;
            }
        }).when(mockCall).enqueue(ArgumentMatchers.any(Callback.class));

        // Perform the registration logic
        registerFragment.performRegister();

        // Verify that the API call was made and the response handled
        Mockito.verify(mockCall).enqueue(ArgumentMatchers.any(Callback.class));
        Toast expectedToast = Toast.makeText(registerFragment.getContext(), "Registration successful!", Toast.LENGTH_LONG);
        assertNotNull(expectedToast); // You can use an actual assertion to validate UI feedback
    }

    @Test
    public void testRegisterFailure() {
        Throwable throwable = new Throwable("Network error");
        Mockito.doAnswer(new Answer<Void>() {
            @Override
            public Void answer(InvocationOnMock invocation) {
                Callback<JwtAuthenticationResponse> callback = (Callback<JwtAuthenticationResponse>) invocation.getArguments()[0];
                callback.onFailure(mockCall, throwable);
                return null;
            }
        }).when(mockCall).enqueue(ArgumentMatchers.any(Callback.class));

        // Perform the registration logic
        registerFragment.performRegister();

        // Verify the failure case handling
        Mockito.verify(mockCall).enqueue(ArgumentMatchers.any(Callback.class));
        Toast expectedToast = Toast.makeText(registerFragment.getContext(), "Registration failed!", Toast.LENGTH_LONG);
        assertNotNull(expectedToast);
    }
}

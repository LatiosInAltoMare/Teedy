package com.sismics.docs.rest.resource;

import com.sismics.docs.core.constant.Constants;
import com.sismics.docs.core.dao.UserDao;
import com.sismics.docs.core.dao.UserRegisterRequestDao;
import com.sismics.docs.core.model.jpa.User;
import com.sismics.docs.core.model.jpa.UserRegisterRequest;
import com.sismics.docs.rest.constant.BaseFunction;
import com.sismics.rest.exception.ClientException;
import com.sismics.rest.exception.ForbiddenClientException;
import com.sismics.rest.exception.ServerException;
import com.sismics.rest.util.ValidationUtil;
import com.sismics.util.JsonUtil;
import jakarta.json.Json;
import jakarta.json.JsonArrayBuilder;
import jakarta.json.JsonObjectBuilder;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Response;

import java.util.Date;
import java.util.List;

/**
 * User registration request REST resources.
 *
 * @author jtremeaux
 */
@Path("/user/register_request")
public class UserRegisterRequestResource extends BaseResource {

    /**
     * Submits a new user registration request.
     *
     * @api {post} /user/register_request Submit a registration request
     * @apiName PostUserRegisterRequest
     * @apiGroup UserRegisterRequest
     * @apiParam {String{3..50}} username Requested username
     * @apiParam {String{1..100}} email E-mail
     * @apiSuccess {String} status Status OK
     * @apiError (client) ValidationError Validation error
     * @apiError (client) AlreadyExistingUsername Username already requested
     * @apiError (client) AlreadyExistingEmail Email already requested
     * @apiPermission none
     * @apiVersion 1.5.0
     *
     * @param username Requested username
     * @param email E-mail
     * @return Response
     */
    @POST
    public Response submit(
            @FormParam("username") String username,
            @FormParam("email") String email) {
        // Validate input data
        username = ValidationUtil.validateLength(username, "username", 3, 50);
        ValidationUtil.validateUsername(username, "username");
        email = ValidationUtil.validateLength(email, "email", 1, 100);
        ValidationUtil.validateEmail(email, "email");

        // Check for existing user or request
        UserDao userDao = new UserDao();
        if (userDao.getActiveByUsername(username) != null) {
            throw new ClientException("AlreadyExistingUsername", "Username already used");
        }
        UserRegisterRequestDao requestDao = new UserRegisterRequestDao();
        if (requestDao.getByEmail(email) != null) {
            throw new ClientException("AlreadyExistingEmail", "Email already requested");
        }

        // Create the registration request
        UserRegisterRequest request = new UserRegisterRequest();
        request.setUsername(username);
        request.setEmail(email);
        request.setStatus("PENDING");
        requestDao.create(request);

        // Return OK
        JsonObjectBuilder response = Json.createObjectBuilder()
                .add("status", "ok");
        return Response.ok().entity(response.build()).build();
    }

    /**
     * Lists all pending registration requests.
     *
     * @api {get} /user/register_request/list List pending registration requests
     * @apiName GetUserRegisterRequestList
     * @apiGroup UserRegisterRequest
     * @apiSuccess {Object[]} requests List of pending requests
     * @apiSuccess {String} requests.id Request ID
     * @apiSuccess {String} requests.username Requested username
     * @apiSuccess {String} requests.email E-mail
     * @apiSuccess {Number} requests.create_date Creation date (timestamp)
     * @apiError (client) ForbiddenError Access denied
     * @apiPermission admin
     * @apiVersion 1.5.0
     *
     * @return Response
     */
    @GET
    @Path("list")
    public Response list() {
        if (!authenticate()) {
            throw new ForbiddenClientException();
        }
        checkBaseFunction(BaseFunction.ADMIN);

        UserRegisterRequestDao requestDao = new UserRegisterRequestDao();
        List<UserRegisterRequest> requests = requestDao.getPendingRequests();
        JsonArrayBuilder requestArray = Json.createArrayBuilder();
        for (UserRegisterRequest request : requests) {
            requestArray.add(Json.createObjectBuilder()
                    .add("id", request.getId())
                    .add("username", request.getUsername())
                    .add("email", request.getEmail())
                    .add("create_date", request.getCreateDate().getTime()));
        }

        JsonObjectBuilder response = Json.createObjectBuilder()
                .add("requests", requestArray);
        return Response.ok().entity(response.build()).build();
    }

    /**
     * Approves a user registration request.
     *
     * @api {post} /user/register_request/:id/approve Approve a registration request
     * @apiName PostUserRegisterRequestApprove
     * @apiGroup UserRegisterRequest
     * @apiParam {String} id Request ID
     * @apiParam {String{8..50}} password Initial password
     * @apiParam {Number} storage_quota Storage quota (in bytes)
     * @apiSuccess {String} status Status OK
     * @apiError (client) ForbiddenError Access denied
     * @apiError (client) RequestNotFound Request not found
     * @apiError (client) ValidationError Validation error
     * @apiError (client) AlreadyExistingUsername Username already used
     * @apiError (server) UnknownError Unknown server error
     * @apiPermission admin
     * @apiVersion 1.5.0
     *
     * @param id Request ID
     * @param password Initial password
     * @param storageQuotaStr Storage quota
     * @return Response
     */
    @POST
    @Path("{id: [a-zA-Z0-9\\-]+}/approve")
    public Response approve(
            @PathParam("id") String id,
            @FormParam("password") String password,
            @FormParam("storage_quota") String storageQuotaStr) {
        if (!authenticate()) {
            throw new ForbiddenClientException();
        }
        checkBaseFunction(BaseFunction.ADMIN);

        // Validate input data
        password = ValidationUtil.validateLength(password, "password", 8, 50);
        Long storageQuota = ValidationUtil.validateLong(storageQuotaStr, "storage_quota");

        // Get the request
        UserRegisterRequestDao requestDao = new UserRegisterRequestDao();
        UserRegisterRequest request = requestDao.getById(id);
        if (request == null || !"PENDING".equals(request.getStatus())) {
            throw new ClientException("RequestNotFound", "Request not found or already processed");
        }

        // Create the user
        User user = new User();
        user.setRoleId(Constants.DEFAULT_USER_ROLE);
        user.setUsername(request.getUsername());
        user.setPassword(password);
        user.setEmail(request.getEmail());
        user.setStorageQuota(storageQuota);
        user.setOnboarding(true);

        UserDao userDao = new UserDao();
        try {
            userDao.create(user, principal.getId());
        } catch (Exception e) {
            if ("AlreadyExistingUsername".equals(e.getMessage())) {
                throw new ClientException("AlreadyExistingUsername", "Username already used");
            } else {
                throw new ServerException("UnknownError", "Unknown server error", e);
            }
        }

        // Update the request status
        request.setStatus("APPROVED");
        request.setProcessDate(new Date());
        requestDao.update(request);

        // Return OK
        JsonObjectBuilder response = Json.createObjectBuilder()
                .add("status", "ok");
        return Response.ok().entity(response.build()).build();
    }

    /**
     * Rejects a user registration request.
     *
     * @api {post} /user/register_request/:id/reject Reject a registration request
     * @apiName PostUserRegisterRequestReject
     * @apiGroup UserRegisterRequest
     * @apiParam {String} id Request ID
     * @apiSuccess {String} status Status OK
     * @apiError (client) ForbiddenError Access denied
     * @apiError (client) RequestNotFound Request not found
     * @apiPermission admin
     * @apiVersion 1.5.0
     *
     * @param id Request ID
     * @return Response
     */
    @POST
    @Path("{id: [a-zA-Z0-9\\-]+}/reject")
    public Response reject(@PathParam("id") String id) {
        if (!authenticate()) {
            throw new ForbiddenClientException();
        }
        checkBaseFunction(BaseFunction.ADMIN);

        // Get the request
        UserRegisterRequestDao requestDao = new UserRegisterRequestDao();
        UserRegisterRequest request = requestDao.getById(id);
        if (request == null || !"PENDING".equals(request.getStatus())) {
            throw new ClientException("RequestNotFound", "Request not found or already processed");
        }

        // Update the request status
        request.setStatus("REJECTED");
        request.setProcessDate(new Date());
        requestDao.update(request);

        // Return OK
        JsonObjectBuilder response = Json.createObjectBuilder()
                .add("status", "ok");
        return Response.ok().entity(response.build()).build();
    }
}
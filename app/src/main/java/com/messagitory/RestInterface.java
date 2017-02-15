package com.messagitory;


import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;
import retrofit2.http.Url;

/**
 * Created by BrillBrains-4 on 22-11-2016.
 */

public interface RestInterface {
    @FormUrlEncoded
    @POST("Service/Messagitory/ListWithLikesAndReports")
    Call<ArrayList<MessageReponse>> getMessageList(@Field("device_id") String device_id);

    @FormUrlEncoded
    @POST("Service/Messagitory/ListForUser")
    Call<ArrayList<MessageReponse>> getMessageListForUser(@Field("device_id") String device_id);


    @FormUrlEncoded
    @POST("Service/Messagitory/NewMessage")
    Call<String> addMessage(@Field("device_id") String device_id, @Field("message") String message);

    @FormUrlEncoded
    @POST("Service/Messagitory/NoofMessages")
    Call<String> noofMessages(@Field("device_id") String device_id);

    @FormUrlEncoded
    @POST
    Call<String> like(@Field("device_id") String device_id, @Url String url);

    @FormUrlEncoded
    @POST
    Call<String> report(@Field("device_id") String device_id, @Url String url);

//    /**
//     * Declaration of call to get vendors.
//     *
//     * @param accessToken access token
//     * @param company_id  According to this id vendor details will come.
//     * @return List of the vendors.
//     */
//    @FormUrlEncoded
//    @POST("Service/Vendor/paging")
//    Call<VendorList> getVendors(@Field("access_token") String accessToken, @Field("company_id") String company_id);

//    /**
//     * Declaration of call to get Products from server.
//     *
//     * @param accessToken access token
//     * @param vendor_id   According to this id products details will come.
//     * @return list of products
//     */
//    @FormUrlEncoded
//    @POST("Service/Product/pagingForPurchase")
//    Call<PurchaseProductData> getProducts(@Field("access_token") String accessToken, @Field("vendor_id") String vendor_id, @Field("company_id") String company_id);
//
//    /**
//     * @param access_token access token
//     * @param json         mJson object containing data to add purchase.
//     * @return Response Code and Message
//     */
//    @FormUrlEncoded
//    @POST("Service/Purchase/NewSave")
//    Call<ServerResponse> addPurchase(@Field("access_token") String access_token, @Field("data") String json);//, @Field("mInvoice") Invoice mInvoice, @Field("mItems") Items[] mItems, @Field("otheramount") String otheramount, @Field("taxtotal") String taxtotal, @Field("subtotal") String subtotal, @Field("vendor") String vendor);
//
//    @FormUrlEncoded
//    @POST
//    Call<String> getInvoiceNumber(@Field("access_token") String access_token, @Url String url);
//
//    /**
//     * Declaration of call to get Products from server.
//     *
//     * @param accessToken access token
//     * @param vendor_id   According to this id products details will come.
//     * @return list of products
//     */
//    @FormUrlEncoded
//    @POST("Service/Product/pagingForSales")
//    Call<ProductList> getProductsForSales(@Field("access_token") String accessToken, @Field("vendor_id") String vendor_id);
//
//    /**
//     * @param access_token access token
//     * @param json         mjson object containing data to add sale.
//     * @return Response Code and Message
//     */
//    @FormUrlEncoded
//    @POST("Service/Sales/NewSave")
//    Call<ServerResponse> addSale(@Field("access_token") String access_token, @Field("data") String json);//, @Field("mInvoice") Invoice mInvoice, @Field("mSaleItems") Items[] mSaleItems, @Field("mOtherAmount") String mOtherAmount, @Field("mTaxTotal") String mTaxTotal, @Field("mSubTotal") String mSubTotal, @Field("vendor") String vendor);
//
//    @FormUrlEncoded
//    @POST("Service/Info/getCountries")
//    Call<CountryData> getCountries(@Field("access_token") String access_token);
//
//    @FormUrlEncoded
//    @POST
//    Call<StatesData> getStates(@Field("access_token") String access_token, @Url String url);
//
//    @FormUrlEncoded
//    @POST
//    Call<CityData> getCities(@Field("access_token") String access_token, @Url String url);
//
//    @FormUrlEncoded
//    @POST
//    Call<CustomerList> getCustomers(@Field("access_token") String access_token, @Url String url);
//
//    @FormUrlEncoded
//    @POST("Service/Channel/MarketplaceAccounts")
//    Call<AccountList> getAccounts(@Field("access_token") String access_token, @Field("code") String code, @Field("company_id") String company_id);
//
//    @FormUrlEncoded
//    @POST("Service/Orders/GetOrdersByAccount")
//    Call<OrderByAccountResponse> getOrders(@Field("access_token") String access_token, @Field("created_by") String created_by, @Field("company_id") String company_id, @Field("accountId") String account_id, @Field("select") String select, @Field("search") String search);
//
//    @FormUrlEncoded
//    @POST("Service/Orders/getData")
//    Call<OrderDetailsResponse> getOrderDetails(@Field("access_token") String access_token, @Field("orderId") String orderId);
//
//    @FormUrlEncoded
//    @POST("Service/Salesreturn/NewSave")
//    Call<ServerResponse> addSalesReturn(@Field("access_token") String access_token, @Field("orderId") String orderId, @Field("created_by") String created_by, @Field("company_id") String company_id, @Field("accountId") String account_id, @Field("data") String data, @Field("channel_id") String channel_id);
//
//    /**
//     * Call declaration for adding new vendor.
//     *
//     * @param access_token access token
//     * @param data         mJson object containing data to add vendor.
//     * @return Response Code and Message
//     */
//    @FormUrlEncoded
//    @POST("Service/Vendor/create")
//    Call<ServerResponse> addVendor(@Field("access_token") String access_token, @Field("data") String data);//, @Field("mAddress") String mAddress, @Field("contact_name") String contact_name, @Field("contact_number") String contact_no, @Field("mEmail") String mEmail, @Field("bank_name") String mBankName, @Field("acc_number") String acc_no, @Field("ifs_code") String ifs_code, @Field("company_id") String company_id, @Field("created_by") String created_by);
//
//    /**
//     * Call declaration for chec king sku mSKUPrefix is available or not.
//     *
//     * @param access_token access token
//     * @param company_id   company id
//     * @param prefix       sku mSKUPrefix to be checked
//     * @return Response Code and Message
//     */
//    @FormUrlEncoded
//    @POST("Service/Vendor/checkPrefix")
//    Call<ServerResponse> checkPrefix(@Field("access_token") String access_token, @Field("company_id") String company_id, @Field("prefix") String prefix);//, @Field("mAddress") String mAddress, @Field("contact_name") String contact_name, @Field("contact_number") String contact_no, @Field("mEmail") String mEmail, @Field("bank_name") String mBankName, @Field("acc_number") String acc_no, @Field("ifs_code") String ifs_code, @Field("company_id") String company_id, @Field("created_by") String created_by);
//
//    @FormUrlEncoded
//    @POST("Service/Orders/getOrderDimensions")
//    Call<ArrayList<OrderItemsDimesions>> getDimensions(@Field("access_token") String access_token, @Field("company_id") String company_id, @Field("created_by") String created_by, @Field("orderId") String orders);
//
//    @FormUrlEncoded
//    @POST("Service/Orders/changeStatus")
//    Call<ServerResponse> changeStatus(@Field("access_token") String accessToken, @Field("new_status") String status, @Field("orderids") String orderids);
//
//    /**
//     * Call declaration for getting purchase details.
//     *
//     * @param access_token access token
//     * @param url
//     * @return Purchase details
//     */
//    @FormUrlEncoded
//    @POST
//    Call<EditPurchaseResponse> getPurchaseData(@Field("access_token") String access_token, @Url String url);
//
//    /**
//     * Call declaration to update purchase details
//     *
//     * @param access_token access token
//     * @param json         mJSON object containing data to update purchase details.
//     * @param url
//     * @return Response code and Message
//     */
//    @FormUrlEncoded
//    @POST
//    Call<ServerResponse> updatePurchaseData(@Field("access_token") String access_token, @Field("data") String json, @Url String url);
//
//    /**
//     * Call declaration for getting sale details.
//     *
//     * @param access_token access token
//     * @param url
//     * @return Sale details
//     */
//    @FormUrlEncoded
//    @POST
//    Call<EditSaleResponse> getSalesData(@Field("access_token") String access_token, @Url String url);
//
//    /**
//     * Call declaration to update sale details
//     *
//     * @param access_token access token
//     * @param json         mJson object containing data to update sale details.
//     * @param url
//     * @return Response code and Message
//     */
//    @FormUrlEncoded
//    @POST
//    Call<ServerResponse> updateSalesData(@Field("access_token") String access_token, @Field("data") String json, @Url String url);
//
//
//    /**
//     * Declaration of call to get Products from server.
//     *
//     * @param accessToken access token
//     * @param vendor_id   According to this id products details will come.
//     * @return list of products
//     */
//    @FormUrlEncoded
//    @POST("Service/Product/VendorSkuData")
//    Call<ProductList> getProductsForVendor(@Field("access_token") String accessToken, @Field("vendor_id") String vendor_id, @Field("company_id") String company_id);
//
//    @FormUrlEncoded
//    @POST("Service/Product/VendorSkuData")
//    Call<ProductList> getProducts(@Field("access_token") String accessToken, @Field("vendor_id") String vendor_id);
//
//    @FormUrlEncoded
//    @POST("Service/Vendor/update")
//    Call<ServerResponse> updateVendor(@Field("access_token") String accessToken, @Field("data") String pojo);//, @Field("mAddress") String mAddress, @Field("contact_name") String contact_name, @Field("contact_number") String contact_no, @Field("mEmail") String mEmail, @Field("bank_name") String mBankName, @Field("acc_number") String acc_no, @Field("ifs_code") String ifs_code, @Field("mID") String mID);
//
//    @FormUrlEncoded
//    @POST("Service/inventory/syncInventory")
//    Call<ServerResponse> syncInventory(@Field("access_token") String accessToken, @Field("company_id") String company_id, @Field("created_by") String created_by);
//
//    @FormUrlEncoded
//    @POST("Service/inventory/updateInventory")
//    Call<UpdateInventoryResponse> updateInventory(@Field("access_token") String accessToken, @Field("data") String data, @Field("created_by") String created_by);
//
//    @FormUrlEncoded
//    @POST("Service/inventory_bulk/paging2")
//    Call<BulkInventoryResponse> getBulkInventory(@Field("access_token") String accessToken, @Field("company_id") String company_id, @Field("limit") String created_by, @Field("offset") String offset);
//
//    @FormUrlEncoded
//    @POST("Service/marketplaceInventory/UpdateToAll")
//    Call<ServerResponse> updateBulkInventory(@Field("access_token") String accessToken, @Field("company_id") String company_id, @Field("created_by") String created_by, @Field("data") String data);
//
//    @FormUrlEncoded
//    @POST("Service/inventory/getInventoryHistory")
//    Call<InventoryHistoryData> getInventoryHistory(@Field("access_token") String accessToken, @Field("inventory_id") String inventory_id, @Field("limit") String limit, @Field("offset") String offset);
//
//    @FormUrlEncoded
//    @POST("Service/Product/update")
//    Call<ServerResponse> updateProduct(@Field("access_token") String accessToken, @Field("data") String data);
//
//    @FormUrlEncoded
//    @POST("Service/Product/unit")
//    Call<UnitList> getUnits(@Field("access_token") String accessToken);
//
//    @FormUrlEncoded
//    @POST("Service/Product/subcategory")
//    Call<ItemCategoryResponse> getSubCategories(@Field("access_token") String accessToken, @Field("categoryId") String categoryId);
//
//    @FormUrlEncoded
//    @POST("Service/Product/paging")
//    Call<ItemsListing> getItems(@Field("access_token") String accessToken, @Field("company_id") String company_id, @Field("limit") String limit, @Field("offset") String offset);
//
//
//    @FormUrlEncoded
//    @POST("Service/Product/create")
//    Call<ServerResponse> addProduct(@Field("access_token") String accessToken, @Field("data") String data);
//
//    @FormUrlEncoded
//    @POST("Service/Product/create")
//    Call<ServerResponse> addProductsInBulk(@Field("access_token") String accessToken, @Field("data") String company_id);
//
//    @FormUrlEncoded
//    @POST("Service/Product/category")
//    Call<ItemCategoryResponse> getCategories(@Field("access_token") String accessToken);
//
//    @FormUrlEncoded
//    @POST("Service/skuMapping/getItemSku")
//    Call<ArrayList<ProductItem>> getItemSKU(@Field("access_token") String access_token, @Field("company_id") String company_id);
//
//    @FormUrlEncoded
//    @POST("Service/Orders/orderProcessingItems")
//    Call<OrderProcessingData> getOrderProcessingItems(@Field("access_token") String access_token, @Field("company_id") String company_id, @Field("created_by") String created_by, @Field("orderId") String orders, @Field("mktCode") String channel_id);
//
//    @FormUrlEncoded
//    @POST("Service/skuMapping/map_sku")
//    Call<ServerResponse> mapChannelSKU(@Field("access_token") String access_token, @Field("data") String data);
//
//    @FormUrlEncoded
//    @POST("Service/Product/category")
//    Call<ItemCategoryResponse> getCategories(@Field("access_token") String accessToken, @Field("company_id") String company_id, @Field("code") String code);
//
//    @FormUrlEncoded
//    @POST("Service/Product/updateCommissionRate")
//    Call<ServerResponse> updateCommissionRate(@Field("access_token") String accessToken, @Field("data") String data);
//
//    /**
//     * Retrofit call to gets user logged in into the system.
//     *
//     * @param email Email ID
//     * @param pwd   Password
//     * @return true if user gets logged in successfully in the system.
//     */
//    @FormUrlEncoded
//    @POST("Account/services_login")
//    Call<LoginResponse> login(@Field("login_email") String email, @Field("login_password") String pwd);
//
//    @FormUrlEncoded
//    @POST("Service/Orders/getCentralStatus")
//    Call<OrderStatus> getOrders(@Field("access_token") String access_token);
//
//
//    @FormUrlEncoded
//    @POST("Service/Marketplace/list")
//    Call<MarketPlace> getMarketPlace(@Field("access_token") String access_token);
//
//    @FormUrlEncoded
//    @POST
//    Call<UserDetails> getUserData(@Field("access_token") String access_token, @Url String url);
//
//    @FormUrlEncoded
//    @POST("Service/Orders/OrdersProcessingPagingByCentralStatus")
//    Call<OrderFilterList> getOrdersForNotification(@Field("limit") String limit, @Field("offset") String offset, @Field("access_token") String access_token, @Field("OrderStatus") String orderstatus, @Field("sku") String sku, @Field("channel_id") String channel_id, @Field("account_id") String account_id, @Field("date") String date, @Field("sla") String sla);
//
//    @FormUrlEncoded
//    @POST("Service/MarketPlace/Snapdeal/Snapdeal_print_manifast")
//    Call<SnapdealManifestResponse> getSnapdealManifest(@Field("access_token") String access_token, @Field("order_ids") String orders, @Field("accountId") String account_id);
//
//    @FormUrlEncoded
//    @POST("Service/MarketPlace/Flipkart/printLabelsNInvoices")
//    Call<FlipkartPDFResponse> getFlipkartManifest(@Field("access_token") String access_token, @Field("orderIds") String orders, @Field("print_status_type") String status);
//
//    @FormUrlEncoded
//    @POST("Service/MarketPlace/Flipkart/printLabelsNInvoices")
//    Call<PrintPDFResponse> getFlipkartLabelandInvoice(@Field("access_token") String access_token, @Field("orderIds") String orders, @Field("print_status_type") String status, @Field("company_id") String company_id, @Field("created_by") String created_by);
//
//    @FormUrlEncoded
//    @POST("Service/Payment/list")
//    Call<PaymentDetails> getPayments(@Field("length") String limit, @Field("start") String offset, @Field("access_token") String access_token, @Field("channel_id") String channel_id, @Field("account_id") String account_id, @Field("date") String date, @Field("company_id") String company_id);
//
//    @FormUrlEncoded
//    @POST("Service/MarketPlace/Snapdeal/Snapdeal_manifast")
//    Call<HandoverCodeResponse> getHandoverCode(@Field("access_token") String access_token, @Field("order_ids") String orders, @Field("accountId") String account_id);
//
//    @FormUrlEncoded
//    @POST("Service/getOrdersByFeedId")
//    Call<ArrayList<String>> getOrderByFeedID(@Field("access_token") String access_token, @Field("feedId") String feedId);
//
//    @FormUrlEncoded
//    @POST("Service/Purchase/paging")
//    Call<PurchaseList> getPurchase(@Field("access_token") String accessToken, @Field("company_id") String company_id);
//
//    @FormUrlEncoded
//    @POST("Service/Sales/paging")
//    Call<SalesList> getSales(@Field("access_token") String accessToken, @Field("company_id") String company_id);
//
//    @FormUrlEncoded
//    @POST("Service/Salesreturn/paging")
//    Call<SalesReturnListing> getSalesReturns(@Field("access_token") String accessToken, @Field("company_id") String company_id, @Field("limit") String limit, @Field("offset") String offset);
//
//    @FormUrlEncoded
//    @POST
//    Call<ArrayList<SalesReturnOrderItem>> getItems(@Field("access_token") String access_token, @Field("company_id") String company_id, @Url String url);
//
//    @FormUrlEncoded
//    @POST
//    Call<ServerResponse> updateSalesReturn(@Field("access_token") String access_token, @Field("data") String data, @Url String url);
//
//    @FormUrlEncoded
//    @POST("Service/skuMapping/getLinkedSku")
//    Call<ConnectedSkuListing> loadConnectedSKU(@Field("access_token") String access_token, @Field("item_id") String item_id);//, @Field("address") String address, @Field("contact_name") String contact_name, @Field("contact_number") String contact_no, @Field("email") String email, @Field("bank_name") String bname, @Field("acc_number") String acc_no, @Field("ifs_code") String ifs_code, @Field("company_id") String company_id, @Field("created_by") String created_by);
//
//    @FormUrlEncoded
//    @POST("Service/skuMapping/map_vendore")
//    Call<ServerResponse> mapSKU(@Field("access_token") String access_token, @Field("data") String data);//, @Field("address") String address, @Field("contact_name") String contact_name, @Field("contact_number") String contact_no, @Field("email") String email, @Field("bank_name") String bname, @Field("acc_number") String acc_no, @Field("ifs_code") String ifs_code, @Field("company_id") String company_id, @Field("created_by") String created_by);
//
//    @FormUrlEncoded
//    @POST("Service/skuMapping/getviewComboItems")
//    Call<ArrayList<ConnectedComboItem>> getConnectedItems(@Field("access_token") String access_token, @Field("comoid") String comoid);
//
//    @FormUrlEncoded
//    @POST("Service/Marketplace/Amazon/processOrder")
//    Call<ArrayList<AmazonConfirmOrderProcess>> processOrders(@Field("access_token") String access_token, @Field("company_id") String company_id, @Field("created_by") String created_by, @Field("order_data") String orders);
//
//    @FormUrlEncoded
//    @POST("Service/Marketplace/Snapdeal/Snapdeal_Print")
//    Call<PrintLabelResponse> getSnapdealLabel(@Field("access_token") String access_token, @Field("order_ids") String orders);
//
//    @FormUrlEncoded
//    @POST("Service/MarketPlace/Flipkart/printLabelsNInvoices")
//    Call<ServerResponse> generateFlipkartLabelNInvoice(@Field("access_token") String access_token, @Field("orderIds") String orders, @Field("print_status_type") String status, @Field("company_id") String company_id, @Field("created_by") String created_by);
//
//    @FormUrlEncoded
//    @POST("Service/Product/commission")
//    Call<CommissionResponse> getCommission(@Field("access_token") String accessToken, @Field("subCategoryId") String subCategoryId);
//
//    @FormUrlEncoded
//    @POST("Service/inventory/paging")
//    Call<Inventory> getInventory(@Field("access_token") String accessToken, @Field("company_id") String company_id, @Field("limit") String limit, @Field("offset") String offset, @Field("account_id") String account_id, @Field("mktCode") String mktCode, @Field("status") String status);
//
//    @FormUrlEncoded
//    @POST("Service/Orders/PagingForView")
//    Call<OrderFilterList> getOrders(@Field("length") String limit, @Field("start") String offset, @Field("access_token") String access_token, @Field("OrderStatus") String orderstatus, @Field("sku") String sku, @Field("channel_id") String channel_id, @Field("account_id") String account_id, @Field("date") String date, @Field("sla") String sla);
//
//    @FormUrlEncoded
//    @POST("Service/skuMapping/paging")
//    Call<ChannelSKUData> getChannelSKUs(@Field("access_token") String access_token, @Field("company_id") String company_id);
//
//    @FormUrlEncoded
//    @POST("Service/skuMapping/getComboList")
//    Call<ComboSKUItems> getComboList(@Field("access_token") String access_token, @Field("company_id") String company_id, @Field("created_by") String created_by);
//
//    @FormUrlEncoded
//    @POST("Service/skuMapping/getMarketSku")
//    Call<ItemSKUCombo> getMarketSKU(@Field("access_token") String access_token, @Field("company_id") String company_id, @Field("user_id") String usre_id, @Field("mktsku") String mksku);
//
//    @FormUrlEncoded
//    @POST("Service/skuMapping/getItemSkuList")
//    Call<ComboDropDownItemList> getItemSku(@Field("access_token") String access_token, @Field("company_id") String company_id, @Field("user_id") String usre_id);
//
//    @FormUrlEncoded
//    @POST("Service/skuMapping/addCombos")
//    Call<ServerResponse> addComboSku(@Field("access_token") String access_token, @Field("company_id") String company_id, @Field("created_by") String created_by, @Field("market_place_account_id") String market_place_account_id, @Field("market_place_sku") String market_place_sku, @Field("marketplace_code") String marketplace_code, @Field("itemSkuid") String itemSkuid);
//
//    @FormUrlEncoded
//    @POST("Service/Marketplace/Amazon/listFeeds")
//    Call<FeedListing> getFeeds(@Field("length") String limit, @Field("start") String offset, @Field("access_token") String access_token, @Field("OrderStatus") String orderstatus, @Field("sku") String sku, @Field("channel_id") String channel_id, @Field("account_id") String account_id, @Field("date") String date, @Field("sla") String sla, @Field("company_id") String company_id);
//
//    @FormUrlEncoded
//    @POST("Service/skuMapping/loaditemskuListing")
//    Call<LinkedItemListing> loadItemSku(@Field("access_token") String access_token, @Field("company_id") String company_id);
//
//    @FormUrlEncoded
//    @POST("Service/Orders/PagingForProcess")
//    Call<OrderFilterList> getOrdersForProcess(@Field("length") String limit, @Field("start") String offset, @Field("access_token") String access_token, @Field("OrderStatus") String orderstatus, @Field("sku") String sku, @Field("channel_id") String channel_id, @Field("account_id") String account_id, @Field("date") String date, @Field("sla") String sla);
//
//    @FormUrlEncoded
//    @POST("Service/Orders/skuWiseOrders")
//    Call<OrderFilterList> getSkuWiseOrders(@Field("length") String limit, @Field("start") String offset, @Field("access_token") String access_token, @Field("OrderStatus") String orderstatus, @Field("sku") String sku, @Field("channel_id") String channel_id, @Field("account_id") String account_id, @Field("date") String date, @Field("sla") String sla, @Field("skudata") String skudata);
//
//    @FormUrlEncoded
//    @POST("Service/Payment/list")
//    Call<PaymentDetails> getPayments(@Field("length") String limit, @Field("start") String offset, @Field("access_token") String access_token, @Field("OrderStatus") String orderstatus, @Field("sku") String sku, @Field("channel_id") String channel_id, @Field("account_id") String account_id, @Field("date") String date, @Field("sla") String sla);
//
//    @FormUrlEncoded
//    @POST("Service/skuMapping/loadVendoreItemSku")
//    Call<VendorItemData> getVendorSKU(@Field("access_token") String access_token, @Field("company_id") String company_id);
//
//    @FormUrlEncoded
//    @POST("Service_Refreshtoken")
//    Call<RefreshTokenResponse> refreshToken(@Field("refresh_token") String refresh_token, @Field("client_id") String client_id, @Field("client_secret") String client_secret);

}

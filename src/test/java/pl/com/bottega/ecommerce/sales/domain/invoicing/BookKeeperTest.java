package pl.com.bottega.ecommerce.sales.domain.invoicing;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;
import pl.com.bottega.ecommerce.sales.domain.productscatalog.ProductData;
import pl.com.bottega.ecommerce.sales.domain.productscatalog.ProductType;
import pl.com.bottega.ecommerce.sharedkernel.Money;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.mockito.Mockito.times;

@RunWith(MockitoJUnitRunner.class)
public class BookKeeperTest {

    @Test
    public void testCase1() {
        //Given
        final Money totalCost = new Money(10);
        final int quantity = 1;
        final Tax tax = getTax();

        final BookKeeper bookKeeper = getBookKeeper();
        final ProductData productData = getProductData();
        final RequestItem requestItem = getRequestItem(totalCost, quantity, productData);
        final InvoiceRequest invoiceRequest = getInvoiceRequest(Collections.singletonList(requestItem));
        final TaxPolicy taxPolicy = getTaxPolicy(totalCost, tax);
        //When
        final Invoice issuance = bookKeeper.issuance(invoiceRequest, taxPolicy);
        //Then
        assertThat(issuance.getItems(), hasSize(1));
    }

    @Test
    public void testCase2() {
        //Given
        final Money totalCost = new Money(10);
        final int quantity = 1;
        final Tax tax = getTax();

        final BookKeeper bookKeeper = getBookKeeper();
        final ProductData productData = getProductData();
        final RequestItem requestItem = getRequestItem(totalCost, quantity, productData);
        final InvoiceRequest invoiceRequest = getInvoiceRequest(Arrays.asList(requestItem, requestItem));
        final TaxPolicy taxPolicy = getTaxPolicy(totalCost, tax);
        //When
        final Invoice issuance = bookKeeper.issuance(invoiceRequest, taxPolicy);
        //Then
        Mockito.verify(taxPolicy, times(2)).calculateTax(ProductType.STANDARD, totalCost);
        assertThat(issuance.getItems(), hasSize(2));
    }

    private Tax getTax() {
        final Tax tax = Mockito.mock(Tax.class);
        Mockito.when(tax.getAmount()).thenReturn(new Money(0));
        Mockito.when(tax.getDescription()).thenReturn("description");
        return tax;
    }

    private BookKeeper getBookKeeper() {
        final InvoiceFactory invoiceFactory = new InvoiceFactory();
        return new BookKeeper(invoiceFactory);
    }

    private TaxPolicy getTaxPolicy(Money totalCost, Tax tax) {
        final TaxPolicy taxPolicy = Mockito.mock(TaxPolicy.class);
        Mockito.when(taxPolicy.calculateTax(ProductType.STANDARD, totalCost)).thenReturn(tax);
        return taxPolicy;
    }

    private InvoiceRequest getInvoiceRequest(List<RequestItem> requestItemList) {
        final InvoiceRequest invoiceRequest = Mockito.mock(InvoiceRequest.class);
        Mockito.when(invoiceRequest.getItems()).thenReturn(requestItemList);
        return invoiceRequest;
    }

    private ProductData getProductData() {
        final ProductData productData = Mockito.mock(ProductData.class);
        Mockito.when(productData.getType()).thenReturn(ProductType.STANDARD);
        return productData;
    }

    private RequestItem getRequestItem(Money totalCost, int quantity, ProductData productData) {
        final RequestItem requestItem = Mockito.mock(RequestItem.class);
        Mockito.when(requestItem.getTotalCost()).thenReturn(totalCost);
        Mockito.when(requestItem.getProductData()).thenReturn(productData);
        Mockito.when(requestItem.getQuantity()).thenReturn(quantity);
        return requestItem;
    }
}
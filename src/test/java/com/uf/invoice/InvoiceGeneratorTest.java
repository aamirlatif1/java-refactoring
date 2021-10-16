package com.uf.invoice;

import com.uf.data.Invoice;
import com.uf.data.Performance;
import com.uf.data.Play;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class InvoiceGeneratorTest {

    private InvoiceGenerator generator;

    @BeforeEach
    void setUp() {
        generator = new InvoiceGenerator();
    }

    @Test
    void unknownTypeInInvoice(){
        //Given
        var invoice = new Invoice("BigCo", List.of(
                new Performance("hamlet", 55),
                new Performance("as-like", 35),
                new Performance("othello", 40)
        ));
        var plays = Map.of("hamlet", new Play("Hamlet", "sci-fi"),
        "as-like", new Play("As You Like It", "comedy"),
        "othello", new Play("Othello", "tragedy")
        );

        //When
        Exception exception = assertThrows(IllegalArgumentException.class, () -> generator. statement(invoice, plays));

        //Then
        Assertions.assertEquals("unknown type: sci-fi", exception.getMessage());
    }

    @Test
    void playIdNotFoundInPerformances(){
        //Given
        var invoice = new Invoice("BigCo", List.of(
                new Performance("hamlet2", 55),
                new Performance("as-like", 35),
                new Performance("othello", 40)
        ));
        var plays = Map.of("hamlet", new Play("Hamlet", "sci-fi"),
                "as-like", new Play("As You Like It", "comedy"),
                "othello", new Play("Othello", "tragedy")
        );

        //When
        Exception exception = assertThrows(IllegalArgumentException.class, () -> generator. statement(invoice, plays));

        //Then
        Assertions.assertEquals("unknown type: hamlet2", exception.getMessage());
    }

    @Test
    void generateStatementSuccess(){
        //Given
        var invoice = new Invoice("BigCo", List.of(
                new Performance("hamlet", 55),
                new Performance("as-like", 35),
                new Performance("othello", 40)
        ));
        var plays = Map.of("hamlet", new Play("Hamlet", "tragedy"),
                "as-like", new Play("As You Like It", "comedy"),
                "othello", new Play("Othello", "tragedy")
        );

        //When
        String actual = generator.statement(invoice, plays);

        //Then
        String expected =
                "Statement for BigCo\n" +
                " Hamlet: $650.00 (55 seats)\n" +
                " As You Like It: $580.00 (35 seats)\n" +
                " Othello: $500.00 (40 seats)\n" +
                "Amount owed is $1,730.00\n" +
                "You earned 47 credits\n";

        Assertions.assertEquals(expected, actual);
    }

}
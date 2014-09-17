package ar.com.gcaba.devops;

import org.junit.runner.JUnitCore;
import org.junit.runner.Result;
import org.junit.runner.notification.Failure;

public class SadeTestRunner
{

	public static void main(String[] args)
	{

		Result result = JUnitCore.runClasses(SadeAllTests.class);
		for (Failure failure : result.getFailures())
		{
			System.out.println( "Fail: " + failure.getMessage());
		}
		System.out.println( (result.wasSuccessful())?"Test exitoso!":"Algunos tests fallaron.");
	}

}

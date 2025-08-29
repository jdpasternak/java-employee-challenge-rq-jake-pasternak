package com.reliaquest.api.service;

import com.reliaquest.api.exception.DownstreamUnavailableException;
import com.reliaquest.api.exception.EmployeeNotFoundException;
import com.reliaquest.api.exception.ValidationException;
import com.reliaquest.api.gateway.EmployeeClient;
import com.reliaquest.api.model.CreateEmployeeInput;
import com.reliaquest.api.model.Employee;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.reliaquest.api.model.SearchInput;
import jakarta.validation.ConstraintViolationException;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class EmployeeServiceTest {

    @InjectMocks
    EmployeeService employeeService;

    @Mock
    EmployeeClient client;

    List<Employee> testEmployees;

    @BeforeEach
    void setUp() {
        Employee nullSalaryEmployee = new Employee();
        Employee duplicateSalaryEmployee = new Employee(
                "99eff840-bc7d-4a3e-b9c8-b46bbcc41043",
                "Shardo Gibsona",
                378048,
                32,
                "Legacy Accounting Executor",
                "sup-ex@company.com");
        Employee duplicate10thSalaryEmployee = new Employee(
                "1500c807-a43a-4d73-9862-a3c4b9331d78",
                "Andrio Schmidt PhD",
                275740,
                47,
                "Consulting Facilition Manager",
                "lotstrung@company.com");
        testEmployees = List.of(
                // 0 - sal_6(tie), sub_sal_3
                new Employee(
                        "99eff840-bc7d-4a3e-b9c8-b46bbcc41042",
                        "Sharda Gibson",
                        378048,
                        31,
                        "Legacy Accounting Executive",
                        "sub-ex@company.com"),
                // 1 - sal_10, sub_sal_5, Highest Salary of sublist(0,5) and overall.
                new Employee(
                        "f5651c6b-e001-46ed-b455-6b4613825de3",
                        "Vena Dickens IV",
                        490233,
                        45,
                        "Forward Healthcare Strategist",
                        "blade_runnerz@company.com"),
                // 2 - sal_9, sub_sal_4
                new Employee(
                        "a552a9a2-44a6-4ff4-80ea-28747a70e661",
                        "Natalia Wyman",
                        433856,
                        56,
                        "International Analyst",
                        "cardguard@company.com"),
                // 3 - sal_4, sub_sal_2
                new Employee(
                        "824af032-38bc-444a-8baa-46e41e24ae4e",
                        "Randall Batz",
                        367484,
                        28,
                        "IT Architect",
                        "veribet@company.com"),
                // 4 - sal_, sub_sal_1, Lowest Salary of sublist(0,5)
                new Employee(
                        "84f48b4c-150a-47b3-bb73-8be88655eb45",
                        "Ms. Gladys Schaden",
                        126899,
                        65,
                        "Accounting Assistant",
                        "lotstring@company.com"),
                // 5 - sal_1
                new Employee(
                        "1500c807-a43a-4d73-9862-a3c4b9331d77",
                        "Andria Schmidt PhD",
                        275740,
                        46,
                        "Consulting Facilitator",
                        "lotstring@company.com"),
                // 6 - sal_
                new Employee(
                        "561d7025-4128-4009-8ec0-2d9fa6c2f429",
                        "Gino Hagenes",
                        108360,
                        42,
                        "Mining Representative",
                        "sub-ex@company.com"),
                // 7 - sal_7
                new Employee(
                        "f450a809-ac1c-46a9-9d97-552dc738fde4",
                        "Zandra Stiedemann",
                        413932,
                        17,
                        "Future Healthcare Orchestrator",
                        "cookley@company.com"),
                // 8 - sal_5
                new Employee(
                        "f243211a-748e-4e06-bd0e-a31a8fd5515e",
                        "Magaly Huels III",
                        377301,
                        51,
                        "Mining Agent",
                        "y-find@company.com"),
                // 9 - sal_8
                new Employee(
                        "639fd1cc-3fda-4035-893e-e41437ba50a0",
                        "Janiece Braun",
                        423689,
                        50,
                        "Central Officer",
                        "colonelkickass@company.com"),
                // 10 - sal_2
                new Employee(
                        "63d8895b-e5fb-4af4-9acf-c40f8b11fd21",
                        "Antoine Leannon",
                        334095,
                        25,
                        "Senior Technology Designer",
                        "bitchin_blair@company.com"),
                // 11 - sal_3
                new Employee(
                        "fc17c110-12ed-4a9f-849f-d24a84c0096f",
                        "Eboni Graham",
                        345847,
                        50,
                        "Farming Facilitator",
                        "prodder@company.com"),
                // 12 - sal_
                new Employee(
                        "824af032-38bc-1234-8baa-46e41e24ae4e",
                        "Brandy Marsh",
                        367484,
                        28,
                        "IT Architect",
                        "cremefraiche@company.com"),
                // 13 - sal_null
                nullSalaryEmployee,
                // 14 sal_6(tie)
                duplicateSalaryEmployee,
                // 15 sal_10(tie)
                duplicate10thSalaryEmployee);
    }

    @AfterEach
    void tearDown() {}

    @Nested
    class FindAllTests {
        @Test
        void findAll_whenNoEmployees_returnsEmptyList_andDelegatesToClient() throws DownstreamUnavailableException {
            // Given
            Mockito.when(client.getAll()).thenReturn(new ArrayList<>());

            // When
            List<Employee> result = employeeService.findAll();

            // Then
            Assertions.assertNotNull(result);
            Assertions.assertTrue(result.isEmpty());

            Mockito.verify(client).getAll();
            Mockito.verifyNoMoreInteractions(client);
        }

        @Test
        void findAll_whenEmployeesExist_returnsList_andDelegatesToClient() throws DownstreamUnavailableException {
            // Given
            List<Employee> employees = testEmployees.subList(0, 2);
            Mockito.when(client.getAll()).thenReturn(employees);

            // When
            List<Employee> result = employeeService.findAll();

            // Then
            Assertions.assertNotNull(result);
            Assertions.assertEquals(2, result.size());

            Mockito.verify(client).getAll();
            Mockito.verifyNoMoreInteractions(client);
        }

        @Test
        void findAll_whenClientRateLimited_throwsDownstreamUnavailableException() throws DownstreamUnavailableException {
            // Given
            Mockito.when(client.getAll()).thenThrow(new DownstreamUnavailableException());

            // When
            Assertions.assertThrows(DownstreamUnavailableException.class, () -> employeeService.findAll());

            // Then
            Mockito.verify(client).getAll();
            Mockito.verifyNoMoreInteractions(client);
        }
    }

    @Nested
    @SpringBootTest
    class SearchTests {
        @MockBean
        EmployeeClient client;

        @Autowired
        EmployeeService employeeService;
        @Test
        void search_whenBlank_throwsConstraintViolationException() {
            // Given
            SearchInput searchInput = new SearchInput(" ");

            // When

            // Then
            Assertions.assertThrows(ConstraintViolationException.class, () -> employeeService.search(searchInput));
            Mockito.verifyNoInteractions(client);
        }

        @Test
        void search_whenCaseIgnoreCase_returnsMatches() throws DownstreamUnavailableException {
            // Given
            List<Employee> result = new ArrayList<>();
            List<Employee> expected = List.of(testEmployees.get(3), testEmployees.get(12));
            SearchInput searchInput = new SearchInput("Rand");
            Mockito.when(client.getAll()).thenReturn(expected);

            // When
            result = employeeService.search(searchInput);

            // Then
            Assertions.assertNotNull(result);
            Assertions.assertFalse(result.isEmpty());
            Assertions.assertEquals(expected.get(0).getName(), result.get(0).getName());
            Assertions.assertEquals(expected.get(1).getName(), result.get(1).getName());

            Mockito.verify(client).getAll();
            Mockito.verifyNoMoreInteractions(client);
        }

        @Disabled
        @Test
        void search_whenNameHasDiacritics_returnsMatches() throws DownstreamUnavailableException {
            // Given
            List<Employee> expected = List.of(testEmployees.get(6));
            SearchInput searchInput = new SearchInput("Gíno");

            // When
            List<Employee> result = employeeService.search(searchInput);

            // Then
            Assertions.assertNotNull(result);
            Assertions.assertFalse(result.isEmpty());
            Assertions.assertEquals(1, result.size());
            Assertions.assertEquals(expected.get(0).getName(), result.get(0).getName());

            Mockito.verify(client).getAll();
            Mockito.verifyNoMoreInteractions(client);
        }

        @Test
        void search_whenNoEmployeesExist_returnsEmptyList() throws DownstreamUnavailableException {
            // Given
            List<Employee> result;
            SearchInput searchInput = new SearchInput("Gíno");
            Mockito.when(client.getAll()).thenReturn(new ArrayList<>());

            // When
            result = employeeService.search(searchInput);

            // Then
            Assertions.assertNotNull(result);
            Assertions.assertTrue(result.isEmpty());

            Mockito.verify(client).getAll();
            Mockito.verifyNoMoreInteractions(client);
        }
    }

    @Nested
    class FindByIdTests {
        @Test
        void findById_whenInvalidUuid_throwsValidationException() {
            // Given
            String nonUuid = "somenonUUID1234$%=";

            // When

            // Then
            Assertions.assertThrows(ValidationException.class, () -> employeeService.findById(nonUuid));
            Mockito.verifyNoInteractions(client);
        }

        @Test
        void findById_whenExists_returnsEmployee() throws EmployeeNotFoundException {
            // Given
            String id = "99eff840-bc7d-4a3e-b9c8-b46bbcc41042";
            String name = "Sharda Gibson";
            Mockito.when(client.getById(Mockito.eq(id))).thenReturn(testEmployees.get(0));

            // When
            Employee result = employeeService.findById(id);

            // Then
            Assertions.assertNotNull(result);
            Assertions.assertEquals(id, result.getId());
            Assertions.assertEquals(name, result.getName());

            Mockito.verify(client.getById(id));
            Mockito.verifyNoInteractions(client);
        }

        @Test
        void findById_whenNoEmployeeExists_throwEmployeeNotFoundException() {
            // Given
            String id = "99eff840-bc7d-4a3e-b9c8-b46bbcc4104f";
            Mockito.when(client.getById(Mockito.eq(id))).thenReturn(null);

            // When

            // Then
            Assertions.assertThrows(EmployeeNotFoundException.class, () -> employeeService.findById(id));

            Mockito.verify(client.getById(id));
            Mockito.verifyNoMoreInteractions(client);
        }
    }

    @Nested
    class FindHighestSalaryOfEmployeesTests {
        @Test
        void findHighestSalaryOfEmployees_whenNoEmployeesExist_returnsEmptyOptional() {
            // Given
            Mockito.when(client.getAll()).thenReturn(new ArrayList<>());

            // When
            Optional<Integer> result = employeeService.findHighestSalaryOfEmployees();

            // Then
            Assertions.assertFalse(result.isPresent());

            Mockito.verify(client.getAll());
            Mockito.verifyNoMoreInteractions(client);
        }

        @Test
        void findHighestSalaryOfEmployees_whenEmployeesWithSalariesExist_returnsHighestSalary() {
            // Given
            Mockito.when(client.getAll()).thenReturn(testEmployees);

            // When
            Optional<Integer> result = employeeService.findHighestSalaryOfEmployees(); // 490233, idx 1

            // Then
            Assertions.assertTrue(result.isPresent());
            Assertions.assertEquals(testEmployees.get(1).getSalary(), result.get());

            Mockito.verify(client.getAll());
            Mockito.verifyNoMoreInteractions(client);
        }

        @Test
        void findHighestSalaryOfEmployees_whenAllSalariesNull_returnsEmptyOptional() {
            // Given
            List<Employee> employeesWithNullSalaries = List.of(new Employee(), new Employee(), new Employee());
            Mockito.when(client.getAll()).thenReturn(employeesWithNullSalaries);

            // When
            Optional<Integer> result = employeeService.findHighestSalaryOfEmployees(); // 490233, idx 1

            // Then
            Assertions.assertFalse(result.isPresent());

            Mockito.verify(client.getAll());
            Mockito.verifyNoMoreInteractions(client);
        }
    }

    @Nested
    class FindTopTenHighestEarningEmployeesTests {
        @Test
        void findTopTenHighestEarningEmployees_whenNoEmployeesExist_returnsEmptyList() {
            // Given
            Mockito.when(client.getAll()).thenReturn(new ArrayList<>());

            // When
            List<Employee> result = employeeService.findTopTenHighestEarningEmployees();

            // Then
            Assertions.assertNotNull(result);
            Assertions.assertTrue(result.isEmpty());

            Mockito.verify(client.getAll());
            Mockito.verifyNoMoreInteractions(client);
        }

        @Test
        void findTopTenHighestEarningEmployees_whenEmployeesListSmall_returnsFewerThanTen() {
            // Given
            List<Employee> employees = testEmployees.subList(0, 5);
            List<Employee> employeesExpectedOrder =
                    List.of(employees.get(1), employees.get(2), employees.get(0), employees.get(3), employees.get(4));
            Mockito.when(client.getAll()).thenReturn(employees);

            // When
            List<Employee> result = employeeService.findTopTenHighestEarningEmployees();

            // Then
            Assertions.assertNotNull(result);
            Assertions.assertFalse(result.isEmpty());
            Assertions.assertEquals(5, result.size());
            Assertions.assertEquals(
                    employeesExpectedOrder.get(0).getSalary(), result.get(0).getSalary());
            Assertions.assertEquals(
                    employeesExpectedOrder.get(1).getSalary(), result.get(1).getSalary());
            Assertions.assertEquals(
                    employeesExpectedOrder.get(2).getSalary(), result.get(2).getSalary());
            Assertions.assertEquals(
                    employeesExpectedOrder.get(3).getSalary(), result.get(3).getSalary());
            Assertions.assertEquals(
                    employeesExpectedOrder.get(4).getSalary(), result.get(4).getSalary());

            Mockito.verify(client.getAll());
            Mockito.verifyNoMoreInteractions(client);
        }

        @Test
        void findTopTenHighestEarningEmployees_whenSalariesTie_sortsBySalaryDescThenNameAscThenIdAsc() {
            // Given
            List<Employee> employeesExpectedOrder = List.of(
                    testEmployees.get(0),
                    testEmployees.get(0),
                    testEmployees.get(0),
                    testEmployees.get(0),
                    testEmployees.get(0),
                    testEmployees.get(0),
                    testEmployees.get(0),
                    testEmployees.get(0),
                    testEmployees.get(0),
                    testEmployees.get(0));
            Mockito.when(client.getAll()).thenReturn(testEmployees);

            // When
            List<Employee> result = employeeService.findTopTenHighestEarningEmployees();

            // Then
            Assertions.assertNotNull(result);
            Assertions.assertFalse(result.isEmpty());
            Assertions.assertEquals(10, result.size());
            Assertions.assertEquals(
                    employeesExpectedOrder.get(0).getSalary(), result.get(0).getSalary());
            Assertions.assertEquals(
                    employeesExpectedOrder.get(1).getSalary(), result.get(1).getSalary());
            Assertions.assertEquals(
                    employeesExpectedOrder.get(2).getSalary(), result.get(2).getSalary());
            Assertions.assertEquals(
                    employeesExpectedOrder.get(3).getSalary(), result.get(3).getSalary());
            Assertions.assertEquals(
                    employeesExpectedOrder.get(4).getSalary(), result.get(4).getSalary());
            Assertions.assertEquals(
                    employeesExpectedOrder.get(5).getSalary(), result.get(5).getSalary());
            Assertions.assertEquals(
                    employeesExpectedOrder.get(6).getSalary(), result.get(6).getSalary());
            Assertions.assertEquals(
                    employeesExpectedOrder.get(7).getSalary(), result.get(7).getSalary());
            Assertions.assertEquals(
                    employeesExpectedOrder.get(8).getSalary(), result.get(8).getSalary());
            Assertions.assertEquals(
                    employeesExpectedOrder.get(9).getSalary(), result.get(9).getSalary());

            Mockito.verify(client.getAll());
            Mockito.verifyNoMoreInteractions(client);
        }

        @Test
        void findTopTenHighestEarningEmployees_whenSalariesTieAtPosition10_useNameAsc() {
            // Given
            List<Employee> employeesExpectedOrder = List.of(
                    testEmployees.get(0),
                    testEmployees.get(0),
                    testEmployees.get(0),
                    testEmployees.get(0),
                    testEmployees.get(0),
                    testEmployees.get(0),
                    testEmployees.get(0),
                    testEmployees.get(0),
                    testEmployees.get(0),
                    testEmployees.get(0));
            Mockito.when(client.getAll()).thenReturn(testEmployees);

            // When
            List<Employee> result = employeeService.findTopTenHighestEarningEmployees();

            // Then
            Assertions.assertNotNull(result);
            Assertions.assertFalse(result.isEmpty());
            Assertions.assertEquals(10, result.size());
            Assertions.assertEquals(
                    employeesExpectedOrder.get(0).getSalary(), result.get(0).getSalary());
            Assertions.assertEquals(
                    employeesExpectedOrder.get(1).getSalary(), result.get(1).getSalary());
            Assertions.assertEquals(
                    employeesExpectedOrder.get(2).getSalary(), result.get(2).getSalary());
            Assertions.assertEquals(
                    employeesExpectedOrder.get(3).getSalary(), result.get(3).getSalary());
            Assertions.assertEquals(
                    employeesExpectedOrder.get(4).getSalary(), result.get(4).getSalary());
            Assertions.assertEquals(
                    employeesExpectedOrder.get(5).getSalary(), result.get(5).getSalary());
            Assertions.assertEquals(
                    employeesExpectedOrder.get(6).getSalary(), result.get(6).getSalary());
            Assertions.assertEquals(
                    employeesExpectedOrder.get(7).getSalary(), result.get(7).getSalary());
            Assertions.assertEquals(
                    employeesExpectedOrder.get(8).getSalary(), result.get(8).getSalary());
            Assertions.assertEquals(
                    employeesExpectedOrder.get(9).getSalary(), result.get(9).getSalary());

            Mockito.verify(client.getAll());
            Mockito.verifyNoMoreInteractions(client);
        }
    }

    @Nested
    class CreateEmployeeTests {
        @Test
        void createEmployee_whenInvalidInput_throwsValidationException() {
            // Given
            CreateEmployeeInput employeeToCreate = new CreateEmployeeInput();

            // When

            // Then
            Assertions.assertThrows(ValidationException.class, () -> employeeService.createEmployee(employeeToCreate));

            Mockito.verifyNoInteractions(client);
        }

        @Test
        void createEmployee_whenValidInput_returnsCreatedEmployee_delegatesToClient() throws ValidationException {
            // Given
            String name = "";
            int salary = 0;
            int age = 0;
            String title = "";
            String email = "";
            String id = ""; // TODO
            CreateEmployeeInput employeeToCreate = new CreateEmployeeInput();
            employeeToCreate.setName(name);
            employeeToCreate.setSalary(salary);
            employeeToCreate.setAge(age);
            employeeToCreate.setTitle(title);

            Employee employeeCreated =
                    new Employee(id, name, salary, age, title, email); // Adds ID, no email since it's generated
            Mockito.when(client.create(employeeToCreate)).thenReturn(employeeCreated);

            // When
            Employee result = employeeService.createEmployee(employeeToCreate);

            // Then
            Assertions.assertNotNull(result);
            Assertions.assertNotNull(result);

            Mockito.verify(client.create(employeeToCreate));
            Mockito.verifyNoMoreInteractions(client);
        }
    }

    @Nested
    class DeleteEmployeeTests {
        @Test
        void deleteEmployeeById_whenNoEmployeeExists_throwsEmployeeNotFoundException() {
            // Given
            String id = "89eff840-bc7d-4a3e-b9c8-b46bbcc41043"; // Does not exist
            Mockito.when(client.getById(id)).thenReturn(null);

            // When
            Assertions.assertThrows(EmployeeNotFoundException.class, () -> employeeService.deleteEmployeeById(id));

            // Then
            Mockito.verify(client.getById(id));
            Mockito.verifyNoMoreInteractions(client);
        }

        @Test
        void deleteEmployeeById_whenNameResolves_deletesByName_returnsTrue() throws EmployeeNotFoundException {
            // Given
            String idToDelete = "99eff840-bc7d-4a3e-b9c8-b46bbcc41042";
            String nameToDelete = "Sharda Gibson";
            Mockito.when(client.getById(idToDelete)).thenReturn(testEmployees.get(0));
            Mockito.when(client.deleteByName(nameToDelete)).thenReturn(true);

            // When
            boolean result = employeeService.deleteEmployeeById(idToDelete);

            // Then
            Assertions.assertTrue(result);

            Mockito.verify(client.getById(idToDelete));
            Mockito.verify(client.deleteByName(nameToDelete));
            Mockito.verifyNoMoreInteractions(client);
        }
    }
}

<form id="trade-row">
    <h2>Help me Resolve the mismatched trade below:</h2>
    <table>
        <thead>
            <tr>
                <th>Trade ID</th>
                <th>Status</th>
                <th>State</th>
                <th>Description</th>
                <th>Portfolio</th>
                <th>Price</th>
                <th>Quantity</th>
                <th>Select</th>
            </tr>
        </thead>
        <tbody>
            <tr>
                <td>${id}</td>
                <td>${status}</td>
                <td>${state}</td>
                <td>${description}</td>
                <td>${portfolio}</td>
                <td>${price}</td>
                <td>${quantity}</td>
                <td>${transaction}</td>
                <td>
                    <button type="action" name="confirm-${id}">
                        CONFIRM
                    </button>
                </td>
            </tr>
            <tr>
                <td>
                    <h3>Enter the updated values:</h3>
                </td>
            </tr>
            <tr>
                <td><text-field name="trade_id" placeholder="New Value"></text-field></td>
                <td><text-field name="status" placeholder="New Value"></text-field></td>
                <td><text-field name="state" placeholder="New Value"></text-field></td>
                <td><text-field name="description" placeholder="New Value"></text-field></td>
                <td><text-field name="portfolio" placeholder="New Value"></text-field></td>
                <td><text-field name="price" placeholder="New Value"></text-field></td>
                <td><text-field name="quanity" placeholder="New Value"></text-field></td>
                <td><text-field name="transaction" placeholder="New Value"></text-field></td>
                <td>
                    <button type="action" name="counterparty-${id}">
                        UPDATE
                    </button>
                </td>
            </tr>
        </tbody>
    </table>
</form>

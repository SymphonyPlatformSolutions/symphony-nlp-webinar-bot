<messageML>
    <form id="trade-row">
        <h2>Help me Resolve the mismatched trade below:</h2>
        <table>
            <thead>
            <tr>
                <td>Trade ID</td>
                <td>Status</td>
                <td>State</td>
                <td>Description</td>
                <td>Portfolio</td>
                <td>Price</td>
                <td>Quantity</td>
                <td>Select</td>
            </tr>
            </thead>
            <tbody>
            <#assign trade = trades>
                <tr>
                    <td>${trade.id}</td>
                    <td>${trade.status}</td>
                    <td>${trade.state}</td>
                    <td>${trade.description}</td>
                    <td>${trade.portfolio}</td>
                    <td>${trade.price}</td>
                    <td>${trade.quantity}</td>
                    <td>${trade.transaction}</td>
                    <td>
                        <button type="action" name="confirm-${trade.id}">CONFIRM</button>
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
                        <button type="action" name="counterparty-${trade.id}">UPDATE</button>
                    </td>
                </tr>
            </tbody>
        </table>
    </form>
</messageML>
